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
              <h1 class="h2 ls-tight">Error Files</h1>
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
              <div class="col-xl-9 col-sm-9"><h5 class="mb-0">My Files</h5></div>
            </div>
            <div class="table-responsive">
              <table class="table table-hover table-nowrap">
                <thead class="table-light">
                  <tr>
                    <th scope="col">File Name</th>
                    <th scope="col">Upload Date</th>
                    <th scope="col">message</th>
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
                    <td>{{file.uploadDate}}</td>
                    <td class="wrong">
                      <b>{{file.message}}</b>
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
  name: "ReferenceItem",
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
                      uploadDate:'2022-04-04',
                      //错误信息
                      message:'Wrong file format',
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
.wrong{
  color:  #f36;
}
</style>