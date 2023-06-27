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
                    <!-- <th scope="col">progress</th> -->
                    <th scope="col">status</th>
                    <th scope="col">Queue</th>
                    <th></th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="file in files.fileList" :key="file.uuid"   
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
                    <!-- <td>
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
                    </td> -->
                    <td>
                      <span class="badge badge-lg badge-dot">
                        <i :class="file.colorClass"></i>{{file.status}}
                      </span>
                    </td>
                    <td>
                      {{file.ranking}}
                    </td>
                    <td class="text-end" >
                      <a v-if="file.status==='Finished'" href="#" class="btn btn-sm btn-neutral operate bg-yellow-500 text-white" 
                        @click.stop="downloadResult(file.uuid,file.status,file.name,file.algorithmsName)">
                        download
                      </a>
                      <!-- <a v-if="file.status==='Finished'" href="#" class="btn btn-sm btn-neutral operate bg-yellow-500 text-white" 
                        @click.stop="downloadCOSFile(file.name)">
                        download
                      </a> -->
                      <a v-if="file.status==='Failed'" href="#" class="btn btn-sm btn-neutral operate bg-red-500 text-white" 
                        @click.stop="downloadResult(file.uuid,file.status,file.name,file.algorithmsName)">
                        error_log
                      </a>
                      <a v-if="file.status==='Submitted' || file.status=== 'Started'" href="#" class="btn btn-sm btn-neutral operate" @click.stop="abortRunner(file.uuid)">stop</a>
                      <button
                        v-if="file.status==='Finished' || file.status=== 'Failed' || file.status=== 'Stopped'"
                        type="button"
                        class=" operate btn btn-sm btn-square btn-neutral text-danger-hover "
                        @click.stop="deleteRunner(file.uuid,file.runnerId,file.status)"
                      >
                        <i class="bi bi-trash"></i>
                      </button>
                    </td>
                  </tr>
                </tbody>
              </table>
              <div class="pagination">
                <el-pagination
                  @current-change="changePage"
                  @size-change="handleSizeChange"
                  :current-page="currentPage"
                  :background="true"
                  layout="sizes, prev, pager, next"
                  :total="total"
                  :page-sizes="[5, 10, 15, 20]"
                  :page-size="pageSize">
                </el-pagination>
              </div>
            </div>
          </div>
          <modal name="as-modal" :scrollable="true" height="auto" >
              <div class="content">
                <i class="bi mybi bi-x-lg off-x" @click="hideModal"  ></i>
                <div class="px-12" style="padding-top: 1rem;" >
                  <h6>Selected:</h6>
                  <div v-for="(gwasAndLDFilename,i) in showDetail.gwasAndLDFilenames" :key="100+i">
                    <div class="list-unstyled mt-2 mb-2 mle"  >
                      <p class="py-1 d-flex align-items-center">
                        <b class="mre" >GWAS:</b>{{ gwasAndLDFilename.gwasFileName }} <b class="mre" v-if="gwasAndLDFilename.ldFileName">,LD:</b>{{ gwasAndLDFilename.ldFileName }}
                      </p>
                    </div>
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
// import CryptoJS from "crypto-js"
import COS from "cos-js-sdk-v5"
import JSZip from 'jszip'
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
        total:0,
        pageSize:5,
        currentPage:1,
        secretId : 'AKIDjLlyxiJgpjRxFI6h6HDuYzBJExRmvra5',
        secretKey : 'JRM3YoqgIiz61kFWYLxMWTFpDOK2Vkkd',
        region :  'ap-nanjing',
        bucketName : 'prs-hub-1316944840'
      }
  },
  methods: {
    downloadResult(uuid,status,name,algorithmsName){
      //加载中
      this.loading=true
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
              uuid:uuid,
              status:status
          },

      } 
      ).then((response) => {
        const content = response 
        const blob = new Blob([content])//构造一个blob对象来处理数据
        const fileName =("Failed" === status)?"error_logs.zip" : name+"_"+algorithmsName+"_result.zip"

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
        this.loading=false

      }).catch((err)=>{
        console.log(err);
        this.loading=false
      })
    },
    // 下载COS上的大文件
    
    downloadCOSFile(jobName) {
      //加载中
      this.loading=true

      const folderKey  = 'webUploadFiles/'; // 文件在COS中的存储路径和名称
      const SecretId = this.secretId
      const SecretKey = this.secretKey
      const Region = this.region
      const Bucket = this.bucketName
      const cosParams = {
        SecretId: SecretId,
        SecretKey: SecretKey,
        Region: Region,
        Bucket: Bucket,
      };

      const cos = new COS(cosParams);
     
      cos.getBucket({
        Bucket: Bucket,
        Region: Region,
        Prefix: folderKey
      }, (err, data) => {
        if (err) {
          console.error(err.message)
          return
        }

        // 创建压缩包
        const zip = new JSZip()

        // 遍历文件列表
        data.Contents.forEach(file => {
          // 下载文件
          cos.getObject({
            Bucket: Bucket,
            Region: Region,
            Key: file.Key
          }, (err, fileData) => {
            if (err) {
              return
            }
            // 将文件添加到压缩包中
            zip.file(file.Key, fileData.Body)

            // 判断是否为最后一个文件，如果是则进行压缩
            if (file === data.Contents[data.Contents.length - 1]) {
              zip.generateAsync({ type: 'blob' }).then(content => {
                // 下载压缩包
                const link = document.createElement('a')
                link.href = URL.createObjectURL(content)
                link.download = jobName+'.zip'
                link.click()
              })
              
              //加载完成
              this.loading=false
            }
          })
        })
      })
    },
    init(currentPage,pageSize){
      let subData = {
          size:pageSize,
          current:currentPage
        }
       Prs.getRunnerStatis(subData).then((response) => {
        if(response.code === 0){
          const data = response.data
          if(data.code === 0){
            this.total = data.total
            this.currentPage=data.current
            this.files.total = data.totalResultsCount

            const runnerList = data.runnerList
            let fileListRes = []

            //定时任务flag
            let stopedTimerFlag = true

            runnerList.forEach(runnerDTO => {
              let file ={}
              file.uuid=runnerDTO.uuid
              file.runnerId=runnerDTO.runnerId
              //文件名
              file.name=runnerDTO.jobName
              file.algorithmsName=runnerDTO.algorithmsName
              file.resultPath=runnerDTO.resultPath

              //上传时间
              file.uploadDate = runnerDTO.createdDate

              const runnerStatus = runnerDTO.runnerStatus
              if(runnerStatus === "0"){
                //状态
                file.status = "Submitted"
                //不同状态对应的样式
                file.colorClass = "bg-secondary"
                //进度 整数:100~0
                file.progress = "0"
                stopedTimerFlag = false
              }else if(runnerStatus === "1"){
                file.status = "Started"
                file.colorClass = "bg-warning"
                file.progress = "10"
                stopedTimerFlag = false
              }else if(runnerStatus === "2"){
                file.status = "Failed"
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
              
              file.ranking=( isEmpty(runnerDTO.runnerQueue)||runnerDTO.runnerQueue===0 || runnerDTO.runnerQueue === "0") ? "--" : runnerDTO.runnerQueue
              file.parameterEnterDTOS = runnerDTO.parameterEnterDTOS
              file.gwasAndLDFilenames = runnerDTO.gwasAndLDFilenames
              fileListRes.push(file)
            });
            this.files.fileList = fileListRes
            
            if(stopedTimerFlag){
              //如果没有在进行中的数据则清除定时器
              clearInterval(this.timer);
            }
          }else{
            clearInterval(this.timer);
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
    //删除数据
    deleteRunner(uuid,runnerId,status){
      let subData = {
        uuid:uuid,
        runnerId:runnerId,
        status:status
      }
      this.$MessageBox.alert("Are you sure you want to delete it !", 'Message', {
        confirmButtonText: 'OK',
        callback: (callVal) => {
          if("confirm" === callVal){
            //加载中
            this.loading=true
            Prs.deleteRunner(subData).then((response) => {
              //加载中
              this.loading=false
              if(response.code === 0){
                const data = response.data
                if(data.code === 0){
                  //提示框
                  this.$message.success("Deleting data Succeeded !")
                  //刷新页面数据
                  this.init(this.currentPage,this.pageSize);
                  this.loading=false
                }
              }
            })
          }
          
        }

      })
      
    },
    //中止
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
                  this.init(this.currentPage,this.pageSize);
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
    },
    //翻页
    changePage(val){
      this.currentPage = val
      this.init(this.currentPage,this.pageSize)
    },
    handleSizeChange(val){
      this.pageSize = val
      this.init(this.currentPage,this.pageSize)
    }
  },
  mounted () {
    this.init(this.currentPage,this.pageSize)

 /*
       最初始情况，项目刚打开的时候，这个时候页面是必定没有定时器的，那么逻辑就会走else，这个时候就会注册一个定时器去循环调用相应逻辑代码
       后续有三种情况
          情况一：路由跳转，跳走了，就要清除这个定时器，所以在beforeDestroy钩子中要清除定时器
          情况二：关闭项目，关闭项目了以后，系统就会自动停掉定时器，这个不用管它
          情况三：刷新页面，这个时候vue组件的钩子是不会执行beforeDestroy和destroyed钩子的，所以
                 需要加上if判断一下，若还有定时器的话，就清除掉，所以这个就是mounted钩子的if判断的原因
    */
    if (this.timer) {
      //清除定时器
      clearInterval(this.timer);
    } else {
      this.timer = setInterval(this.init(this.currentPage,this.pageSize), 1800000);//30分钟定时刷新数据
    }

  },
  
  beforeDestroy() {
    //清除定时器
    clearInterval(this.timer);
  },
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
.mybi{
  float: right;  
  margin-top: 0.5rem; 
  margin-right: 0.5rem;
}
.pagination{
  margin-top: 1rem;
  margin-bottom: 1rem;
}
.el-pagination.is-background .el-pager li:not(.disabled).active {
    background-color: #796CFF !important;
}
</style>