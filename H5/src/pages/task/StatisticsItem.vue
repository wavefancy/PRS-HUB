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
                  <tr v-for="file in files.fileList" :key="file.id">  
                    <td>
                      <div class="d-flex align-items-center">
                        {{file.name}}
                      </div>
                    </td>
                    <td>
                      <div class="d-flex align-items-center">
                        {{file.name}}
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
                    <td class="text-end">

                      <a v-if="file.status==='Finish'" href="#" class="btn btn-sm btn-neutral operate">download</a>
                      <button
                        type="button"
                        class="
                          operate
                          btn btn-sm btn-square btn-neutral
                          text-danger-hover
                        "
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
export default {
  name: "StatisticsItem",
  data() {
      return {
          files:{
              //正在分析的总数
              total:'10',
              //我的任务排名
              ranking:'3',
              fileList:[
              ]
          }
      }
  },
  mounted () {
    Prs.getRunnerStatis().then((response) => {
      console.info(response)
      if(response.code === 0){
        const data = response.data
        if(data.code === 0){
          const runnerStatisDTOList = data.runnerStatisDTOList
          let fileListRes = []
          runnerStatisDTOList.forEach(runnerStatisDTO => {
            let file ={}
            file.id=runnerStatisDTO.uuid
            //文件名
            file.name=runnerStatisDTO.fileName
            //上传时间
            file.uploadDate = runnerStatisDTO.createdDate
            const runnerStatus = runnerStatisDTO.runnerStatus
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
              file.status = "Finish"
              file.colorClass = "bg-success"
              file.progress = "100"
            }
            file.ranking="--"
            fileListRes.push(file)
          });
          this.files.fileList = fileListRes
        }
      }else{
        //跳转登录页面
            //提示框
            this.$MessageBox.alert("The login status is invalid, please log in again !", 'prompt', {
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
  }
};
</script>

<style>
.operate{
  margin-left: 0.5rem;

}
</style>