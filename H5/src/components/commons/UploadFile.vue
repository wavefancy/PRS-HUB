<template>
  <div>
    <h4 class="mb-4">Step 1 Upload GWAS summary statistics</h4>
    <div class="card">
      <div class="card-body pb-0">
        <div class=" rounded
            border-2 border-dashed border-primary-hover
            position-relative " >
          <div class="d-flex justify-content-center px-5 py-5">
            <label
              for="file_upload"
              class=" position-absolute
                w-full h-full
                top-0 start-0
                cursor-pointer" >
              <input
                id="file_upload"
                name="file_upload"
                type="file"
                class="visually-hidden"
                @change="fileChange"/>
              
            </label>
            <div class="text-center">
              <div class="text-2xl text-muted">
                <i class="bi bi-upload"></i>
              </div>
              <div class="d-flex text-sm mt-3">
                <p class="font-semibold">Upload a file</p>
              </div>
              <p class="text-xs text-gray-500">tsv.gz up to 3MB</p>
            </div>
          </div>
        </div>
        <div class="row">
          <div class="col-2  py-3 mt-2" style="width: 6rem">
            <a href="#">template</a>
          </div>
          <!-- Upload preview -->
          <div class="col-10 list-group-list py-3 d-flex align-items-center mt-2" v-if="progressVisible">
            <div class="flex-fill">
              <div class="d-flex align-items-center">
                <!-- Title -->
                <span class="d-block h6 text-sm font-semibold me-2">{{fileName}}:</span>
                <span class="me-2">{{progress}}%</span>
                <div class="me-2">
                  <div class="progress" style="width: 100px">
                    <div
                      class="progress-bar"
                      :class="colorClass"
                      role="progressbar"
                      :aria-valuenow="progress"
                      aria-valuemin="0"
                      aria-valuemax="100"
                      :style="`width:${progress}%`"
                    ></div>
                  </div>
                </div>
                <!-- Subtitle -->
                <span class="d-block text-xs text-muted me-2">{{fileSize}}</span>
                <span class="d-block text-xs text-muted">{{uplodMsg}}</span>
              </div>
            </div>
            
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import axios from "axios"
  import {Decimal} from "decimal.js"
  // import { isEmpty }  from "@/utils/validate"
  Decimal.set({
    rounding: 4
  })
  axios.defaults.timeout = 40000
  axios.defaults.baseURL = process.env.VUE_APP_BASE_PRS_EPORTAL
  axios.defaults.headers['accessToken'] = localStorage.getItem("accessToken")
  export default {
    name: "UploadFile",
    data () {
      return {
        // 上传的文件列表
        fileName:"",
        fileSize:"",
        progress:0,
        progressVisible:false,
        colorClass:'bg-success',
        uplodMsg:""
      }
    },
    methods: {
      fileChange(e){
        let inputDom = e.target // input 本身，从这里获取 files<FileList>
        let file = inputDom.files[0]
        if(file){
          const fileName = file.name
          // const fileType = file.type
          const fileSize = file.size
          
          let formData = new FormData();
          formData.append('file', file);
          formData.append('fileType',"GWAS")
        let config = {
            onUploadProgress: progressEvent => {
              
              this.fileName = fileName
              //属性lengthComputable主要表明总共需要完成的工作量和已经完成的工作是否可以被测量
              //如果lengthComputable为false，就获取不到total和loaded
              if (progressEvent.lengthComputable) {
                this.progressVisible=true
                //progressEvent.loaded:已上传文件大小
                //progressEvent.total:被上传文件的总大小
                let complete = Decimal.div(progressEvent.loaded,progressEvent.total).toFixed(2, Decimal.ROUND_HALF_UP)* 100
                if (complete < 100){
                  this.progress = complete/2;
                  this.uplodMsg = "uploading"
                }else{
                  this.progress = 50;
                  this.uplodMsg = "Data validation"
                }
              }

            },
            headers: {
              'Content-Type': 'multipart/form-data'
            }
          }


          axios.post("/sftpupload",formData,config).then(response => {
            const resData = response
            const code = resData.code;
            if(code === 0){
              const innerData = resData.data;
              const msg = innerData.msg;
              const fileId = innerData.fileId;
              if(innerData.code === 0){
                this.progress = 100;
                
                this.uplodMsg = "successful"
                this.fileSize = fileSize<1024? fileSize+"b" : (Decimal.div(fileSize,1024)<1024 ? Decimal.div(fileSize,1024).toFixed(2, Decimal.ROUND_HALF_UP)+"kb" : Decimal.div(Decimal.div(fileSize,1024),1024).toFixed(2, Decimal.ROUND_HALF_UP)+"mb"  )
                let fileData = {}
                fileData["uploadFlag"] = true
                fileData["fileId"] = fileId
                this.$store.dispatch('uploadFileData/setUploadFlag',fileData)
              }else{
                this.$bus.$emit('tips', {
                  show: true,
                  title: msg,
                  time:2000
                })

              }
            }
            inputDom.value=""//同一文件可重复上传
          })
        }
      }
    }
  };
</script>

<style>
</style>