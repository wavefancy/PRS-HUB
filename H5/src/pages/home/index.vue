<template>
  <div class="bg-surface-secondary">
    <Header></Header>
    <!-- <div class="py-md-32 position-relative" style="padding-top: 0rem!important;"> -->
    <div class=" position-relative" >
        <div class="my-md">
            <div class="container-lg align-items-center">
                <!-- <div class="col-lg-6 order-lg-1 ms-auto d-none d-lg-block">
                    <div class="mb-5 mb-lg-0 w-11/10 position-relative">
                        <div class="svg-fluid position-relative overlap-10">
                            <img src="@/assets/work.png" >
                        </div>
                        <div class="position-absolute bottom-0 start-72 h-64 w-64 mt-n4 transform translate-y-n1/2 translate-x-n1/2 gradient-bottom-right start-purple-400 end-cyan-500 filter blur-100 rounded-4 p-5"> </div>
                    </div>
                </div> -->
                <div class="row order-md-0" style="    text-align: center!important;">
                    <!-- Surtitle -->
                    <h2 class="mb-5 mb-5" style="font-size: 3rem;">
                        Polygenic Risk Score Computing Platform
                    </h2>
                    
                    <!-- Text -->
                    <p class="mb-10" style="font-size: 1.5rem;">
                        <b>SNPs Weights Estimates from Multiple PRS Models at One Time</b>
                    </p>
                    <!-- Buttons -->
                    <div class="mx-n2 mb-20">
                        <router-link to="/login" class="btn btn-lg btn-primary shadow-sm mx-2 px-lg-8" style="font-size: 1.5rem;">
                            Sign in
                        </router-link>
                        <router-link to="/register" class="btn btn-lg btn-neutral mx-2 px-lg-8" style="font-size: 1.5rem;">
                            Register
                        </router-link>
                    </div>
                
                </div>
            </div>
        </div>
        <div style="background-color: #9da0f9;">
            <div class="container py-md-5 " style="    padding-left: 15rem; padding-right: 15rem;">
                <div class="row text-center ">
                    <div class=" col-md-4 text-center">
                        <p class="font-D">
                            <span class="lead" style="font-size: 200%;">{{homeDatas.runnerCount}}</span><br>Completed Jobs
                        </p>
                    </div>
                    <div class=" col-md-4 text-center">
                        <p class="font-D">
                            <span class="lead" style="font-size: 200%;">{{homeDatas.userCount}}</span><br>Registered Users
                        </p>
                    </div>
                    <div class=" col-md-4 text-center">
                        <p class="font-D">
                            <span class="lead" style="font-size: 200%;">{{homeDatas.runningCount}}</span><br>Running Jobs
                        </p>
                    </div>
                </div>
            </div>
        </div>
        <div class="" style="margin-top: 5rem;text-align: center!important;">
            <div class="container-lg   ">
                <h2 class="h2 mb-5 ">The easiest way to compute multiple PRS models at one time</h2>
                <!-- Workflow Description -->
                <div class="card-deck row text-center py-10">
                    <div class="card col-lg-4 box-shadow">
                        <div class="card-body">
                            <p><img src="@/assets/up-cloud.png" width="105px"></p>
                            <b>Upload your  GWAS summary statistics</b> to our platform.<br>All interactions with the platform are <b>secured</b>.
                        </div>
                    </div>
                    <div class="card col-lg-4 box-shadow">
                        <div class="card-body">
                            <p><img src="@/assets/impute.png" width="105px"></p>
                            <b>Choose PRS methods</b>. We will take care of complex pipelines for PRS computation.
                        </div>
                    </div>
                    <div class="card col-lg-4 box-shadow">
                        <div class="card-body">
                            <p><img src="@/assets/down-cloud.png" width="105px"></p>
                            <b>Download the results</b>.<br>We will keep the results for 30 days. After 30 days, all results are deleted from our platform.
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
  </div>
</template>

<script>
import Header from "@/components/commons/HeaderItem.vue"
import {Account} from "@/api"
export default {
    name:"HomeItem",
    components: {
        Header
    },
    data () {
      return {
        imgUrls:{
          primary:"./img/logos/PRS-hub.png"
        },
        homeDatas:[],
      }
    },
    methods: {
        initDatas(){
            Account.homeDatas().then((res) => {
                let code = res.code
                if(code===0){
                    this.homeDatas = res.data;
                }
            })
        }
    },
    mounted() {
        this.initDatas();
    }
}
</script>

<style>
 .logo{
    color:#796CFF;
    flex-wrap: nowrap;
    padding: 0px;
  }
  .prs{
    font-size: larger;
    margin-top: 0rem !important;
    padding: 0px;
    line-height: 2.875rem;
    height:  2.875rem;
    width: auto;
  }
  .card-deck .card {
    display: -ms-flexbox;
    display: flex;
    -ms-flex: 1 0 0%;
    flex: 1 0 0%;
    -ms-flex-direction: column;
    flex-direction: column;
    margin-right: 15px;
    margin-bottom: 0;
    margin-left: 15px;
}
.font-D{
    font-size: 200%;
    color: #fff;
}
.my-md {
    padding-top: 10rem;
}
</style>