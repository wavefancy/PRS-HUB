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
              <h1 class="h2 ls-tight">Reference panel</h1>
            </div>
          </div>
        </div>
      </div>
    </header>
    <!-- Main -->
    <main class="py-6 bg-surface-secondary">
      <div class="container-fluid">
        <h4 class="mb-4">Upload LD reference</h4>
        <div class="card">
          <div class="card-body pb-0">
            <div class="row" style="margin: 0.5rem 0rem;">
              <div class="des"><b>Name:</b></div>
              <div class="col-xl-3 col-sm-6 input-group-sm input-group-inline">
                <input type="text" class="form-control" v-model="fileName"/>
              </div>
            </div>
            <div class="row" style="margin:  0.5rem  0rem;">
              <div class="des"><b>Descrition:</b></div>
              <div class="col-xl-3 col-sm-6 input-group-sm input-group-inline">
                <input type="text" class="form-control" v-model="descrition"/>
              </div>
            </div>
            
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
                  <!-- <p class="text-xs text-gray-500">tsv.gz up to 3MB</p> -->
                </div>
              </div>
            </div>
            <div class="row ">
              <div class="mb-2" style="width: 6rem">
              </div>
              <!-- Upload preview -->
              <div class="col-10 list-group-list py-3 d-flex align-items-center mt-2"  v-if="progressVisible" >
                <div class="flex-fill">
                  <div class="d-flex align-items-center">
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
                    <span class="d-block text-xs text-muted me-2">{{fileSize}}</span>
                    <span class="d-block text-xs text-muted">{{uplodMsg}}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="vstack gap-6 mt-3">
          <h4 class="mt-3">reference table</h4>
          <!-- Table -->
          <div class="card">
            <div class="card-header row">
              <div class="col-xl-9 col-sm-9"><h5 class="mb-0">My reference</h5></div>
            </div>
            <div class="table-responsive">
              <table class="table table-hover table-nowrap">
                <thead class="table-light">
                  <tr>
                    <th scope="col">Name</th>
                    <th scope="col">Descrition</th>
                    <th scope="col">Upload Date</th>
                    <th scope="col">Status</th>
                    <th scope="col">Delete Date</th>
                    <th scope="col">Refresh</th>
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
                      </div>
                    </td>
                    <td>
                      <div class="d-flex align-items-center">
                        {{file.deleteDate}}
                      </div>
                    </td>
                    <td>
                      <a href="#" class="btn btn-sm btn-neutral operate">Refresh</a>
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
  import axios from "axios"
  import {Prs} from "@/api"
  import {Decimal} from "decimal.js"
  // import { isEmpty }  from "@/utils/validate"
  Decimal.set({
    rounding: 4
  })
  axios.defaults.timeout = 40000
  axios.defaults.baseURL = process.env.VUE_APP_BASE_PRS_EPORTAL
  axios.defaults.headers['accessToken'] = localStorage.getItem("accessToken")
  export default {
    name: "ReferenceItem",
    data() {
        return {
            files:{
                fileList:[
                    {
                        id:'001',
                        //文件名
                        name:'testFile1',
                        descrition:'',
                        //上传时间
                        uploadDate:'2022-04-04',
                        //错误信息
                        message:'Wrong file format',
                    }
                ]
            },
            fileSize:'',
            descrition:'',
            fileName:'',
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
          // const fileName = file.name
          // const fileType = file.type
          const fileSize = file.size
          
          let formData = new FormData();
          formData.append('file', file);
          formData.append('fileType',"LD")
          formData.append('descrition',this.descrition)
          formData.append('fileName',this.fileName)
        let config = {
            onUploadProgress: progressEvent => {
              
              // this.fileName = fileName
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
              if(innerData.code === 0){
                this.progress = 100;
                
                this.uplodMsg = "successful"
                this.fileSize = fileSize<1024? fileSize+"b" : (Decimal.div(fileSize,1024)<1024 ? Decimal.div(fileSize,1024).toFixed(2, Decimal.ROUND_HALF_UP)+"kb" : Decimal.div(Decimal.div(fileSize,1024),1024).toFixed(2, Decimal.ROUND_HALF_UP)+"mb"  )
                //刷新table
                this.getFileList()
              }else{
                //提示框
                this.$MessageBox.alert(msg, 'prompt', {
                  confirmButtonText: 'OK',
                  callback: () => {
                    inputDom.value=""//同一文件可重复上传
                  }
                })

              }
            }else{
              this.$MessageBox.alert('System is busy, please try again later !', 'prompt', {
                  confirmButtonText: 'OK',
                  callback: () => {
                    inputDom.value=""//同一文件可重复上传
                  }
                })
            }
            inputDom.value=""//同一文件可重复上传
          })
        }
      },
      getFileList(){
        let subData = {
          // fileType:'GWAS',
          fileType:'LD',
        }
        //提交参数
        Prs.getFileList(subData).then(response => {
          if(response.code===0){
            const resData = response.data;
            if(resData.code===0){
              this.files.fileList=resData.resDTOList
            }
          }
        })
      }
    },
    //数据初始化
    mounted(){
      this.getFileList();
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