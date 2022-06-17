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
              <h1 class="h2 ls-tight">Jobs Statistics</h1>
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
                      <a href="#" class="btn btn-sm btn-neutral operate">download</a>
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
                  {
                      id:'001',
                      //文件名
                      name:'testFile1',
                      //上传时间
                      uploadDate:'2022-03-04',
                      //进度 整数:100~0
                      progress:'100',
                      //状态
                      status:'Finish',
                      //不同状态对应的样式
                      colorClass:'bg-success',
                      ranking:'--'
                  },
                  {
                      id:'002',
                      //文件名
                      name:'testFile2',
                      //上传时间
                      uploadDate:'2022-03-04',
                      //进度 整数:100~0
                      progress:'80',
                      //状态
                      status:'In progress',
                      //不同状态对应的样式
                      colorClass:'bg-warning',
                      ranking:'--'
                  },
                  {
                      id:'003',
                      //文件名
                      name:'testFile3',
                      //上传时间
                      uploadDate:'2022-03-04',
                      //进度 整数:100~0
                      progress:'4',
                      //状态
                      status:'Project at risk',
                      //不同状态对应的样式
                      colorClass:'bg-danger',
                      ranking:'--'
                  },
                  {
                      id:'004',
                      //文件名
                      name:'testFile4',
                      //上传时间
                      uploadDate:'2022-03-04',
                      //进度 整数:100~0
                      progress:'0',
                      //状态
                      status:'Not started',
                      //不同状态对应的样式
                      colorClass:'bg-secondary',
                      ranking:'3'
                  }
              ]
          }
      }
  },
};
</script>

<style>
.operate{
  margin-left: 0.5rem;

}
</style>