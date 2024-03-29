<template>
  <div class="rounded border-2 border-dashed border-primary-hover position-relative mb-6">
    <uploader 
      ref="uploader"
      :options="options"
      :autoStart="false"
      :file-status-text="fileStatusText"
      @file-added="onFileAdded"
      @file-success="onFileSuccess"
      @file-error="onFileError"
      @file-progress="onFileProgress"
      @file-complete="onFileComplete"
      class="">
      <uploader-unsupport></uploader-unsupport>
      <uploader-drop class="d-flex justify-content-center px-5 py-5">
            <uploader-btn :attrs="attrs"
              class="position-absolute w-full h-full top-0 start-0 cursor-pointer">
            </uploader-btn>
            <div class="text-center">
              <div class="text-2xl text-muted">
                <i class="bi bi-upload"></i>
              </div>
              <div class="d-flex text-sm mt-3">
                <p class="font-semibold">Upload a file</p>
              </div>
            </div>
      </uploader-drop>
      <uploader-list></uploader-list>
    </uploader>
  </div>
</template>

<script>
import SparkMD5 from 'spark-md5'
import { isEmpty }  from "@/utils/validate"
import {Prs} from "@/api"
const FILE_UPLOAD_ID_KEY = 'file_upload_id'
// 分片大小，10MB
const CHUNK_SIZE = 10 * 1024 * 1024

  export default {
    name:"VueSimpleUploader",
    props: ['fileName','attr','type','pop'],
    data () {
      return {
        options: {
          // 上传地址
          target: process.env.VUE_APP_BASE_PRS_EPORTAL+'/bigfile/upload',
          // 是否开启服务器分片校验。默认为 true
          testChunks: true,
          // 分片大小
          chunkSize: CHUNK_SIZE,
          // 并发上传数，默认为 3 
          simultaneousUploads: 3,
          //设置header
          headers: { 
              accessToken: localStorage.getItem("accessToken")
          },
          query:{
            fileNameInput: "",
          },
          //单文件上传
          singleFile: true,
          /**
           * 判断分片是否上传，秒传和断点续传基于此方法
           */
          checkChunkUploadedByResponse: (chunk, message) => {
            let messageObj = JSON.parse(message)
            let dataObj = messageObj.data
            if (dataObj.uploaded !== undefined) {
              return dataObj.uploaded
            }
            // 判断文件或分片是否已上传，已上传返回 true
            // 这里的 uploadedChunks 是后台返回
            return (dataObj.uploadedChunks || []).indexOf(chunk.offset + 1) >= 0
          }
        },
        attrs: {
          accept: this.attr
        },
        // 上传状态
        fileStatusTextObj:{
            success: 'success',
            error: 'error',
            uploading: 'uploading',
            paused: 'paused',
            waiting: 'waiting'
        },
        uploadIdInfo: null,
        uploadFileList: [],
        fileChunkList: [],
        fileType:this.type
      }
    },
    
    watch: {
        'fileName': function (val) { //监听props中的属性
            this.options.query.fileNameInput = val;
        },
        'attr': function (val) { //监听props中的属性
            if(!isEmpty(val)){
              this.attrs.accept = val;
            }
        },
        'fileType':function(val){
            this.fileType = val
        }
    },
    methods: {
      onFileAdded(file) {
        if(isEmpty(this.fileName)){
          console.log('录入的文件名fileName=' + this.fileName)
          //暂停上传
          file.pause()
          this.$message({
            message: "Please enter fileName !",
            type: 'error',
            duration: 3 * 1000,
          })
          return false;
        }
        if(this.pop === -1){
          //暂停上传
          file.pause()
          this.$message({
            message: "Please select pop !",
            type: 'error',
            duration: 3 * 1000,
          })
          return false;
        }

        // 有时 fileType为空，需截取字符
        console.log('文件类型：' + file.fileType)
        // 文件大小
        console.log('文件大小：' + file.size + 'B')
        // 1. todo 判断文件类型是否允许上传
        // 2. 计算文件 MD5 并请求后台判断是否已上传，是则取消上传
        console.log('校验MD5')

        this.getFileMD5(file, md5 => {
          if (md5 != '') {
            // 修改文件唯一标识
            file.uniqueIdentifier = md5
            // 恢复上传
            file.resume()
          }
        })
      },
      onFileSuccess(rootFile, file, response, chunk) {
        console.log("上传成功")
        const resJaon = JSON.parse(response)
        console.log("chunk="+chunk)

        
        const data = resJaon.data

        if(!isEmpty(data)){
          const flag = data.flag
          if(flag&&!isEmpty(data.fileName)){
            if("GWAS" === this.fileType){
              let formData = new FormData();
              formData.append('fileName',data.fileName)
              formData.append('filePath',data.filePath)
              formData.append('suffixName',data.suffixName)
              formData.append('identifier',data.identifier)
              //校验文件头部信息
              Prs.checkFileTitle(formData).then(response => {
                const success = response.success
                const resMap = response.data
                const flag = resMap.flag;
                if(success){
                  if(flag){
                    //触发总线绑定的fileChange函数
                    this.$bus.$emit('fileChange',data)
                  }else{
                    // 取消上传且从文件列表中移除
                    file.cancel()
                    const msg = resMap.msg;
                    this.$MessageBox.alert(msg, 'Message', {
                      confirmButtonText: 'OK'
                    })
                  }
                }else{
                  //触发总线绑定的fileChange函数
                  this.$bus.$emit('fileChange',data)
                }
              })
            }else{
              //触发总线绑定的fileChange函数
              this.$bus.$emit('fileChange',data)
            }
          }
        }

      },
      onFileComplete(rootFile){
        console.log(rootFile)
        console.log("上传成功Complete")
      },
      onFileError(rootFile, file, message, chunk) {
        console.log('上传出错：' + message)
        console.log('上传出错chunk：' + chunk)
        //暂停上传
        file.pause()
        },
      onFileProgress(rootFile, file, chunk) {
        console.log(`当前进度：${Math.ceil(file._prevProgress * 100)}%`)
        console.log(`当前进度chunk：`+chunk)
      },
      getFileMD5(file, callback) {
        let spark = new SparkMD5.ArrayBuffer()
        let fileReader = new FileReader()
        let blobSlice = File.prototype.slice || File.prototype.mozSlice || File.prototype.webkitSlice
        let currentChunk = 0
        let chunks = Math.ceil(file.size / CHUNK_SIZE)
        let startTime = new Date().getTime()
        file.pause()
        loadNext()
        fileReader.onload = function(e) {
          spark.append(e.target.result)
          if (currentChunk < chunks) {
            currentChunk++
            loadNext()
          } else {
            let md5 = spark.end()
            console.log(`MD5计算完毕：${md5}，耗时：${new Date().getTime() - startTime} ms.`)
            callback(md5)
          }
        }
        fileReader.onerror = function() {
          this.$message.error('文件读取错误')
          file.cancel()
        }
        function loadNext() {
          const start = currentChunk * CHUNK_SIZE
          const end = ((start + CHUNK_SIZE) >= file.size) ? file.size : start + CHUNK_SIZE
          fileReader.readAsArrayBuffer(blobSlice.call(file.file, start, end))
        }
      },
      fileStatusText(status) {
        if (status === 'md5') {
          return '校验MD5'
        } else {
          return this.fileStatusTextObj[status]
        }
      },
      saveFileUploadId(data) {
        localStorage.setItem(FILE_UPLOAD_ID_KEY, data)
      }
    }
  }
</script>

<style>
  .uploader-drop {
      border: 1px dashed #fff !important;
      background-color: #fff !important;
  }
  .uploader-btn {
      border: 0px solid #fff !important;
  }
  .pagination{
    margin-top: 1rem;
  }
  .el-pagination.is-background .el-pager li:not(.disabled).active {
      background-color: #796CFF !important;
  }
</style>