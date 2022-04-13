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
                <input type="text" class="form-control"/>
              </div>
            </div>
            <div class="row" style="margin:  0.5rem  0rem;">
              <div class="des"><b>Descrition:</b></div>
              <div class="col-xl-3 col-sm-6 input-group-sm input-group-inline">
                <input type="text" class="form-control"/>
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
                  <p class="text-xs text-gray-500">tsv.gz up to 3MB</p>
                </div>
              </div>
            </div>
            <div class="row ">
              <!-- Upload preview -->
              <div class="col-10 list-group-list py-3 d-flex align-items-center mt-2" >
                <!-- <div class="flex-fill">
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
                  </div>
                </div> -->
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
                        {{file.name}}
                      </div>
                    </td>
                    <td></td>
                    <td>{{file.uploadDate}}</td>
                    <td></td>
                    <td></td>
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
    padding: 0.5rem 0rem 0.5rem 1rem;
}
</style>