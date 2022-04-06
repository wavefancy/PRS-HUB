<template>
  <div>
    <h4 class="mb-4">Step 1 Upload GWAS summary statistics</h4>
    <div class="card">
      <div class="card-body pb-0">
        <div
          class="
            rounded
            border-2 border-dashed border-primary-hover
            position-relative
          "
        >
          <div class="d-flex justify-content-center px-5 py-5">
            <label
              for="file_upload"
              class="
                position-absolute
                w-full
                h-full
                top-0
                start-0
                cursor-pointer
              "
            >
              <input
                id="file_upload"
                name="file_upload"
                type="file"
                class="visually-hidden"
                @change="fileChange"
              />
              
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
          <div class="col-10 list-group list-group-flush mt-2">
            <div class="list-group-item py-3 d-flex align-items-center">
              <div class="flex-fill">
                <!-- Title -->
                <span class="d-block h6 text-sm font-semibold mb-1"
                  >{{fileName}}</span
                >
                <!-- Subtitle -->
                <span class="d-block text-xs text-muted">{{fileSize}}</span>
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
axios.defaults.baseURL = "http://127.0.0.1:9090/prs/hub"
axios.defaults.headers.post['Content-Type'] = 'multipart/form-data'
axios.defaults.headers['accessToken'] = localStorage.getItem("accessToken")
export default {
  name: "UploadFile",
  data () {
    return {
      // 上传的文件列表
      fileName:"",
      fileSize:""
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
      
        axios.post("/sftpupload",formData).then(response => {
          const resData = response
          const code = resData.code;
          if(code === 0){
            const innerData = resData.data;
            const msg = innerData.msg;
            const fileId = innerData.fileId;
            if(innerData.code === 0){
              this.$bus.$emit('tips', {
                show: true,
                title: 'File uploaded successfully!',
                time:2000
              })
              this.fileName = fileName
              this.fileSize = fileSize<1024? fileSize+"b" : Decimal.div(fileSize,1024).toFixed(2, Decimal.ROUND_HALF_UP)+"kb"
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