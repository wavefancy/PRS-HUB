<template>
  <!-- Content -->
  <div class="flex-lg-1 h-screen overflow-y-lg-auto">
    <vue-element-loading :active="loading" :is-full-screen="true" spinner="ring"/>
    <!-- Navbar -->
    <header>
      <div class="container-fluid">
        <div class="border-bottom pt-6">
          <div class="row align-items-center">
            <div class="col-md-6 col-12 mb-4 mb-sm-0">
              <!-- Title -->
              <h1 class="h2 ls-tight">Jobs Panel</h1>
            </div>
          </div>
        </div>
      </div>
    </header>
    <!-- Main -->
    <main class="py-6 bg-surface-secondary">
      <div class="container-fluid">
        <div class="vstack gap-6">
          <!-- Table -->
          <div class="card">
            <div class="card-header row">
              <div class="col-xl-9 col-sm-9"><h5 class="mb-0">My jobs</h5></div>
              <div class="col">
                A total of <b>{{files.total}}</b> tasks 
              </div>
            </div>
            <div class="table-responsive">
              <table class="table table-hover table-nowrap">
                <thead class="table-light">
                  <tr>
                    <th scope="col">Job Name</th>
                    <th scope="col">Algorithms Name</th>
                    <th scope="col">Upload Date</th>
                    <th scope="col">progress</th>
                    <th scope="col">status</th>
                    <th scope="col">Queue</th>
                    <th></th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="file in files.fileList" :key="file.id"   
                      @click="setEnterDetail(file)" >  
                    <td>
                      <div class="d-flex align-items-center">
                         {{file.name}}
                      </div>
                    </td>
                    <td>
                      <div class="d-flex align-items-center">
                        {{file.algorithmsName}}
                      </div>
                    </td>
                    <td>{{file.uploadDate}}</td>
                    <td>
                      <div class="d-flex align-items-center">
                        <span class="me-2">{{file.progress}}%</span>
                        <div>
                          <div class="progress" style="width: 100px">
                            <div
                              class="progress-bar"
                              :class="file.colorClass"
                              role="progressbar"
                              :aria-valuenow="file.progress"
                              aria-valuemin="0"
                              aria-valuemax="100"
                              :style="`width:${file.progress}%`"
                            ></div>
                          </div>
                        </div>
                      </div>
                    </td>
                    <td>
                      <span class="badge badge-lg badge-dot">
                        <i :class="file.colorClass"></i>{{file.status}}
                      </span>
                    </td>
                    <td>
                      {{file.ranking}}
                    </td>
                    <td class="text-end" >

                      <a v-if="file.status==='Finished'" href="#" class="btn btn-sm btn-neutral operate" :class="file.btnClass" @click.stop="downloadResult(file.id,file.name,file.algorithmsName)">download</a>
                      <a v-if="file.status==='Not started' || file.status=== 'In progress'" href="#" class="btn btn-sm btn-neutral operate" @click.stop="abortRunner(file.id)">stop</a>
                      <button
                        v-if="file.status==='Finished' || file.status=== 'Project at risk' || file.status=== 'Stopped'"
                        type="button"
                        class=" operate btn btn-sm btn-square btn-neutral text-danger-hover "
                        @click.stop="deleteRunner(file.id,file.status)"
                      >
                        <i class="bi bi-trash"></i>
                      </button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
          <modal name="as-modal" :scrollable="true" height="auto" >
              <div class="content">
                <i class="bi bi-x-lg off-x" @click="hideModal"  ></i>
                <div class="px-12" style="padding-top: 1rem;" >
                  <h6>Selected:</h6>
                  <div class="list-unstyled mt-2 mb-2 mle"  >
                    <p class="py-1 d-flex align-items-center">
                      <b class="mre" >GWAS:</b>{{ showDetail.fileNameGWAS }}
                    </p>
                  </div>
                  <div class="list-unstyled mt-2 mb-2 mle"  >
                    <p class="py-1 d-flex align-items-center">
                    <b class="mre"> LD:</b>{{ showDetail.fileNameLD }}
                    </p>
                  </div>
                  <h6>Parameters:</h6>
                  <div class="list-unstyled mt-2 mb-2 mle" v-for="(parameter,index) in showDetail.parameterEnterDTOS" :key="index">
                    <p class="py-1 d-flex align-items-center">
                      <b class="mre">{{ parameter.parameterName }}:</b>{{ parameter.parameterValue }}
                    </p>
                  </div>
                </div>
              </div>
          </modal>
        </div>
      </div>
    </main>
  </div>
</template>

<script>
import {Prs} from "@/api"
import axios from 'axios'
import {isEmpty }  from "@/utils/validate"

axios.defaults.timeout = 40000
axios.defaults.baseURL = process.env.VUE_APP_BASE_PRS_EPORTAL
axios.defaults.headers.post['Content-Type'] = 'application/json charset=UTF-8'
export default {
  name: "StatisticsItem",
  data() {
      return {
          files:{
              //正在分析的总数
              total:0,
              fileList:[
              ]
          },
        loading:false,
        showDetail:{},
      }
  },
  methods: {
    downloadResult(id,name,algorithmsName){
      //使用原生的axios请求文件为二进制流 
      axios({
          method: "get",
          url: "/downloadResult",
          headers: {
              "content-type": "application/json; charset=utf-8",
              "accessToken":localStorage.getItem("accessToken")
          },
          responseType: "blob",       //设置响应类型为blob，否则二进制流直接转换会出错
          params: { // 其他参数
              uuid:id
          },

      }
      ).then((response) => {
        const content = response 
        const blob = new Blob([content])//构造一个blob对象来处理数据
        const fileName =name+"_"+algorithmsName+"_result.tar.gz"

        //对于<a>标签，只有 Firefox 和 Chrome（内核） 支持 download 属性
        //IE10以上支持blob但是依然不支持download
        if ('download' in document.createElement('a')) { //支持a标签download的浏览器
          const link = document.createElement('a')//创建a标签
          link.download = fileName//a标签添加属性
          link.style.display = 'none'
          link.href = URL.createObjectURL(blob)
          document.body.appendChild(link)
          link.click()//执行下载
          URL.revokeObjectURL(link.href) //释放url
          document.body.removeChild(link)//释放标签
        } else { //其他浏览器
          navigator.msSaveBlob(blob, fileName)
        }

      }).catch((err)=>{
          console.log(err);
      })
    },
    init(){
       Prs.getRunnerStatis().then((response) => {
        if(response.code === 0){
          const data = response.data
          if(data.code === 0){

            this.files.total = data.totalResultsCount

            const runnerList = data.runnerList
            let fileListRes = []
            runnerList.forEach(runnerDTO => {
              let file ={}
              file.id=runnerDTO.uuid
              //文件名
              file.name=runnerDTO.jobName
              file.algorithmsName=runnerDTO.algorithmsName
              file.resultPath=runnerDTO.resultPath
              //上传时间
              file.uploadDate = runnerDTO.createdDate
              const runnerStatus = runnerDTO.runnerStatus
              if(runnerStatus === "0"){
                //状态
                file.status = "Not started"
                //不同状态对应的样式
                file.colorClass = "bg-secondary"
                //进度 整数:100~0
                file.progress = "0"
              }else if(runnerStatus === "1"){
                file.status = "In progress"
                file.colorClass = "bg-warning"
                file.progress = "10"
              }else if(runnerStatus === "2"){
                file.status = "Project at risk"
                file.colorClass = "bg-danger"
                file.progress = "50"
              }else if(runnerStatus === "3"){
                file.status = "Finished"
                file.colorClass = "bg-success"
                file.btnClass = "download"
                file.progress = "100"
              }else if(runnerStatus === "4"){
                file.status = "Stopped"
                file.colorClass = "bg-secondary"
                file.btnClass = "stop"
                file.progress = "20"
              }
              
              file.ranking= isEmpty(runnerDTO.queue)? "--" : runnerDTO.queue
              file.parameterEnterDTOS = runnerDTO.parameterEnterDTOS
              file.fileNameGWAS = runnerDTO.fileNameGWAS
              file.fileNameLD = runnerDTO.fileNameLD
              fileListRes.push(file)
            });
            this.files.fileList = fileListRes
          }
        }else{
          //跳转登录页面
              //提示框
              this.$MessageBox.alert("The login status is invalid, please log in again !", 'Message', {
                confirmButtonText: 'OK',
                callback: () => {
                  //跳转登录页面
                  this.$router.push({
                    name:'login'
                  });
                }

              })
              return
        }
      })
    },
    deleteRunner(uuid,status){
      let subData = {
        uuid:uuid,
        status:status
      }
    
      //加载中
      this.loading=true
      Prs.deleteRunner(subData).then((response) => {
        //加载中
        this.loading=false
        if(response.code === 0){
          const data = response.data
          if(data.code === 0){
            //提示框
              this.$MessageBox.alert("Deleting data Succeeded !", 'Message', {
                confirmButtonText: 'OK',
                callback: () => {
                              
                  //刷新页面数据
                  this.init();
                }

              })
          }
        }
      })
    },
    abortRunner(uuid){
      let subData = {
        uuid:uuid
      }
      
      //加载中
      this.loading=true
      Prs.abortRunner(subData).then((response) => {
        //加载中
        this.loading=false
        if(response.code === 0){
          const data = response.data
          if(data.code === 0){
            //提示框
              this.$MessageBox.alert("Abort Succeeded !", 'Message', {
                confirmButtonText: 'OK',
                callback: () => {
                  //刷新页面数据
                  this.init();
                }

              })
          }
        }
      })
    },
    setEnterDetail(file){
      this.$modal.show('as-modal')
      this.showDetail = file 
    },
    hideModal(){
      this.$modal.hide('as-modal')
    }
  },
  mounted () {
    this.init()
  }
};
</script>

<style scoped >
.operate{
  margin-left: 0.5rem;

}
.download{
  background-color: #13227a;
  color: #ffffff;
}
.stop{
  background-color: rgb(255, 89, 89);
  color: #ffffff;
}
.enterDetail{
    text-align: left;
    width: 50%;
    float: left;
}
.mre{
  margin-right: 1rem;
}
.bi{
  float: right;  
  margin-top: 0.5rem; 
  margin-right: 0.5rem;
}
</style>