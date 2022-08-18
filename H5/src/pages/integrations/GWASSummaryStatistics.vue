<template>
  <!-- Content -->
  <div class="flex-lg-1 h-screen overflow-y-lg-auto">
    <!-- Navbar -->
    <header>
      <div class="container-fluid">
        <div class="border-bottom pt-6">
          <div class="row align-items-center">
            <div class="col-md-6 col-12 mb-4 mb-sm-0">
              <!-- Title -->
              <h1 class="h2 ls-tight">GWAS Summary Statistics panel</h1>
            </div>
          </div>
        </div>
      </div>
    </header>
    <!-- Main -->
    <main class="py-6 bg-surface-secondary">
      <div class="container-fluid">
        <h4 class="mb-4">Upload GWAS Summary Statistics</h4>
        <div class="card">
          <div class="card-body pb-0">
            <div class="row" style="margin: 0.5rem 0rem;">
              <div class="des"><b>Name:</b></div>
              <div class="col-xl-3 col-sm-6 input-group-sm input-group-inline">
                <input type="text" class="form-control" v-model.trim="fileName" ref="fileName" @blur="checkName"/>
              </div>
            </div>
            <div class="row" style="margin:  0.5rem  0rem;">
              <div class="des"><b>Descrition:</b></div>
              <div class="col-xl-3 col-sm-6 input-group-sm input-group-inline">
                <input type="text" class="form-control" v-model.trim="descrition" ref="descrition"/>
              </div>
            </div>
            <VueSimpleUploader :fileName="fileName" :aria-disabled="disabled"></VueSimpleUploader>
          </div>
        </div>
        <div class="vstack gap-6 mt-3">
          <!-- Table -->
          <div class="card">
            <div class="card-header row">
              <div class="col-xl-9 col-sm-9"><h5 class="mb-0">My GWAS Summary Statistics</h5></div>
            </div>
            <div class="table-responsive">
              <table class="table table-hover table-nowrap">
                <thead class="table-light">
                  <tr>
                    <th scope="col-xl-1 col-sm-1">Name</th>
                    <th scope="col-xl-5 col-sm-5">Descrition</th>
                    <th scope="col-xl-2 col-sm-2">Upload Date</th>
                    <th scope="col-xl-1 col-sm-1">Status</th>
                    <th scope="col-xl-2 col-sm-2">Delete Date</th>
                    <th scope="col-xl-1 col-sm-1">operation</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="file in files.fileList" :key="file.id">  
                    <td>
                      <div class="d-flex align-items-center">
                        {{file.fileName}}
                      </div>
                    </td>
                    <td>
                      <div class="d-flex align-items-center">
                        {{file.descrition}}
                      </div>
                    </td>
                    <td>
                      <div class="d-flex align-items-center">
                        {{file.uploadDate}}
                      </div>
                      </td>
                    <td>
                        <div class="d-flex align-items-center">
                            {{file.status}} 
                            <el-tooltip class="item" effect="light" placement="right">
                              <div slot="content">The file is valid for 30 days. <br/>Click "Refresh" for an extension of 30 days !</div>
                              <img :src="hintUrl" style="width: 1rem;">
                            </el-tooltip>
                        </div>
                    </td>
                    <td>
                      <div class="d-flex align-items-center">
                        {{file.deleteDate}}
                      </div>
                    </td>
                    <td>
                      <a href="#" v-if="file.status =='validity'" class="btn btn-sm btn-neutral operate" @click.prevent="extensionFileValidTime(file.id)">Refresh</a>
                      <button
                        type="button"
                        class="
                          operate
                          btn btn-sm btn-square btn-neutral
                          text-danger-hover
                        "
                        @click="deleteFile(file.id,file.identifier)"
                      >
                        <i class="bi bi-trash"></i>
                      </button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script>
  import {Prs} from "@/api"
  import {Decimal} from "decimal.js"
  import { isEmpty }  from "@/utils/validate"
  import { Message } from 'element-ui'
  
  import VueSimpleUploader from '@/components/commons/VueSimpleUploader'
  Decimal.set({
    rounding: 4
  })
  export default {
    name: "GWASSummaryStatistics",
    components: {
      VueSimpleUploader
    },
    data() {
        return {
            files:{
                fileList:[
                ]
            },
            type:"GWAS",
            fileSize:'',
            descrition:'',
            fileName:'',
            progress:0,
            progressVisible:false,
            colorClass:'bg-success',
            uplodMsg:"",
            hintUrl:"./img/hint.png",
            disabled:true
        }
    },
    methods: {
      fileChange(data){
        //校验数据
        if(isEmpty(this.fileName)){
          alert("Please enter fileName !")
          this.$refs.fileName.focus()
          //清空input（file）中已上传的文件
          this.$refs.file_upload.value = '';
          return;
        }
        // if(isEmpty(this.descrition)){
        //   alert("Please enter descrition !")
        //   this.$refs.descrition.focus()
        //   //清空input（file）中已上传的文件
        //   this.$refs.file_upload.value = '';
        //   return;
        // }
        
        let formData = new FormData();
        formData.append('fileType',this.type)
        formData.append('descrition',this.descrition)
        formData.append('fileName',this.fileName)
        formData.append('filePath',data.filePath)
        formData.append('suffixName',data.suffixName)
        formData.append('identifier',data.identifier)

        Prs.savePrsFileInfo(formData).then(response => {
          const resData = response
          const code = resData.code;
          if(code === 0){
            const innerData = resData.data;
            const msg = innerData.msg;
            if(innerData.code === 0){
              this.fileName=""
              this.descrition=""
              //刷新table
              this.getFileList()
            }else{
              //提示框
              this.$MessageBox.alert(msg, 'Message', {
                confirmButtonText: 'OK',
                callback: () => {
                }
              })

            }
          }else{
            this.$MessageBox.alert('System is busy, please try again later !', 'Message', {
                confirmButtonText: 'OK',
                callback: () => {
                }
              })
          }

        })
      },
      getFileList(){
        let subData = {
          fileType:this.type,
        }
        //提交参数
        Prs.getFileList(subData).then(response => {
          if(response.code===0){
            const resData = response.data;
            if(resData.code===0){
              this.files.fileList=resData.resDTOList;
            }
          }
        })
      },
      checkName(){
         //校验数据
        if(isEmpty(this.fileName)){
          return;
        }
        const fileList = this.files.fileList;
        if(fileList.length>0){
          fileList.forEach(file => {
            const fileName = file.fileName;
            if(fileName === this.fileName){
              this.$MessageBox.alert('The file name already exists !', 'Message', {
                confirmButtonText: 'OK',
                  callback: () => {
                    this.fileName = '';
                    this.$refs.fileName.value = '';
                    this.$refs.fileName.innerHTML = '';
                  }
                })
            }
          });
        }
      },
      //删除文件
      deleteFile(id,identifier){
        let subData = {
          fileId:id,
          identifier:identifier,
        }
        Prs.deleteFile(subData).then(response => {
          if(response.code===0){
            const resData = response.data;
            if(resData.code===0){
              Message({
                message: "successful delete !",
                type: 'success',
                duration: 2 * 1000
              })
              this.getFileList();
            }
          }
        })
      },
      //延长文件有效时间
      extensionFileValidTime(id){
        let subData = {
          fileId:id,
        }
        Prs.extensionFileValidTime(subData).then(response => {
          if(response.code===0){
            const resData = response.data;
            if(resData.code===0){
               Message({
                message: "successful validity !",
                type: 'success',
                duration: 2 * 1000
              })
              this.getFileList();
            }
          }
        })
      }
    },
    //数据初始化
    mounted(){
      
      //为总线绑定函数
      this.$bus.$on('fileChange',this.fileChange)
      this.getFileList();
    },
    watch: {
      type:{
         // 数据发生变化就会调用这个函数  
          handler() {
            this.getFileList();
            this.fileSize=''
            this.descrition=''
            this.fileName=''
            this.progress=0
            this.progressVisible=false
            this.colorClass='bg-success'
            this.uplodMsg=""
          },
          // 立即处理 进入页面就触发
          immediate: true
      }
    }
    
  };
</script>

<style>
.operate{
  margin-left: 0.5rem;

}
.wrong{
  color:  #f36;
}
.des{
    -webkit-appearance: none;
    -moz-appearance: none;
    appearance: none;
    background-clip: padding-box;
    background-color: #fff;
    color: #16192c;
    display: block;;
    font-weight: 400;
    line-height: 1.3;
    transition: border-color .15s ease-in-out,box-shadow .15s ease-in-out;
    width: 6rem;
    border-radius: 0.375rem;
    font-size: .875rem;
    padding: 0.5rem 0rem;
}
</style>