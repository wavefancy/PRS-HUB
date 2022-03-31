<template>
    <!-- Content -->
    <div class="flex-lg-1 h-screen overflow-y-lg-auto">
      <!-- Navbar -->
      <header class="position-sticky top-0 overlap-10 bg-surface-primary border-bottom">
        <div class="container-fluid py-4">
          <div class="row align-items-center">
            <div class="col">
              <div class="d-flex align-items-center gap-4">
                
                <h1 class="h4 ls-tight">Algorithms</h1>
              </div>
            </div>
            <div class="col-auto">
              <div class="hstack gap-2 justify-content-end">
                <a href="#!" class="text-sm text-muted text-primary-hover font-semibold me-2 d-none d-md-block">
                  <i class="bi bi-question-circle-fill"></i>
                  <span class="d-none d-sm-inline ms-2">Need help?</span>
                </a>
                <button type="submit" class="btn btn-sm btn-primary"
                  :data-bs-toggle="subModalToggle" 
                  data-bs-target="#modalInstallApp" 
                  @click="clickSubmit"
                  :disabled="submitFlag"
                >
                  <span>submit</span>
                </button>
              </div>
            </div>
          </div> 
        </div>
      </header>
      <!-- Main -->
      <main class="py-6 bg-surface-secondary">
          <!-- 弹层 -->
        <PopModal v-if='showParModal' >
          <div class="modal-body px-12">
            <div class="d-flex flex-column align-items-center text-center">
              <!-- Title -->
              <h5 class="text-center mt-6 mb-4">Please configure your parameters</h5>
            </div>
            <div class="mt-10">
              <!-- Heading -->
              <h6>Parameters:</h6>
              <!-- Features -->
              <div class="row" v-for="parameter in showParameters" :key="parameter.id">
                <label class="col-12 control-label">
                  {{ parameter.name }}:{{ parameter.description }}
                </label>
                <div class="input-group input-group-sm input-group-inline">
                  <input
                    type="text"
                    class="form-control"
                    v-model.trim="parameter.setValue"
                    :placeholder='"Default:"+parameter.defaultValue'
                  />
                </div>
                <label v-show="parameter.errorFlag" class="errorMsg" name="errorMsg">
                  请按照默认值格式输入正确的数据!
                </label>
              </div>
              <div class="modal-footer">
                <button
                  type="button"
                  class="btn btn-sm btn-neutral"
                  data-bs-dismiss="modal"
                >
                  Cancel
                </button>
                <button type="button" data-bs-dismiss="modal" class="btn btn-sm btn-primary"
                  @click="setAlgorithmsData(settingPId)"
                >
                  Save
                </button>
              </div>
            </div>
          </div>
        </PopModal>
        <PopModal v-if='!showParModal'>
          <div class="modal-body px-12" >
            <div class="d-flex flex-column align-items-center text-center">
              <!-- Title -->
              <h5 class="text-center mt-6 mb-4">Please check the algorithms you set and its parameters</h5>
            </div>
            <div class="mt-4 algorithm" v-for="algorithm in algorithmsData" :key="algorithm.id">
              <!-- Heading -->
              <h6>{{algorithm.name}}:</h6>
              <!-- Features -->
              <div class="parameters row" >
                <label class="mt-2 col-12 control-label" style="font-size: smaller"
                       v-for="parameter in algorithm.parameters" :key="parameter.id">
                  <b style="margin-right:10px">{{parameter.name}}:</b>{{parameter.value}}
                </label>
              </div>
            </div>
            <div class="modal-footer " style="border-top-color:#fff !important">
              <button type="button" class="btn btn-sm btn-neutral" data-bs-dismiss="modal" >
                Cancel
              </button>
              <button type="button" class="btn btn-sm btn-primary" data-bs-dismiss="modal" @click="dataSubmit">submit</button>
            </div>
          </div>
        </PopModal>
        <div class="container-fluid vstack gap-10">
          <UploadFile></UploadFile>
          <AlgorithmsItem :algorithmsShow='algorithms.algorithmsShow' ></AlgorithmsItem>
        </div>
      </main>
    </div>
</template>
<script>
import axios from "axios"
import { isEmpty }  from "@/utils/validate"
// import {mapActions,mapGetters} from 'vuex'
import UploadFile from "@/components/commons/UploadFile"
import PopModal from "@/components/commons/PopModal"
import AlgorithmsItem from "@/pages/integrations/AlgorithmsItem"

axios.defaults.timeout = 40000
axios.defaults.baseURL = "http://127.0.0.1:9090/prs/hub"
axios.defaults.headers.post['Content-Type'] = 'application/json charset=UTF-8'
axios.defaults.headers['accessToken'] = localStorage.getItem("accessToken")
export default {
    name:"AnalysisItem",
    components:{
      PopModal,
      UploadFile,
      AlgorithmsItem
    },
    data() {
      return {
        algorithms:{
          //算法参数-页面展示
          algorithmsShow:[
            {
                id :1,
                name:"P+T",
                toggleName:null,//控制展示弹窗
                summary:"P+T is a clumping and thresholding method to derive polygenic scores.<br>Doi: 10.1086/519795",
                parameters:[
                  {
                      id:"001",
                      name:"p1",
                      description:"p-value threshold",
                      defaultValue:"1.0,0.5,0.05,5×10−4,5×10−6,and5×10−8",
                      setValue:null,
                      errorFlag:false
                  },
                  {
                      id:"002",
                      name:"r^2",
                      description:"r2 threshold",
                      defaultValue:"0.2,0.4,0.6,0.8",
                      setValue:null,
                      errorFlag:false,
                      
                  },
                  {
                      id:"003",
                      name:"kb",
                      description:"clump kb radius",
                      defaultValue:"250",
                      setValue:null,
                      errorFlag:false,
                      
                  }
                ]
            },
            {
                id :2,
                name:"LDpred2",
                toggleName:null,
                summary:"LDpred2 is a popular method for deriving polygenic scores based on summary statistics and a linkage disequilibrium (LD) matrix only.<br>Doi: 10.1093/bioinformatics/btaa1029",
                parameters:[
                  {
                      id:"001",
                      name:"p1",
                      description:"p-value threshold",
                      defaultValue:"1.0, 0.5, 0.05, 5 × 10−4, 5 × 10−6, and 5 × 10−8",
                      setValue:null,
                      errorFlag:false
                  }
                ]
            },
            {
                id :3,
                name:"lassosum",
                toggleName:null,
                summary:"lassosum is a method for constructing PGS using summary statistics and a reference panel in a penalized regression framework.<br>Doi: 10.1002/gepi.22050",
                parameters:[
                  {
                      id:"001",
                      name:"p1",
                      description:"p-value threshold",
                      defaultValue:"1.0, 0.5, 0.05, 5 × 10−4, 5 × 10−6, and 5 × 10−8",
                      
                      setValue:null,
                      errorFlag:false,
                      
                  }
                ]
            },
            {
                id :4,
                name:"PRS-CS",
                toggleName:null,
                summary:"PRS-CS utilizes a high dimensional Bayesian regression framework, it place a continuous shrinkage (CS) prior on SNP effect sizes.<br>Doi: 10.1038/s41467-019-09718-5",
                parameters:[
                  {
                      id:"001",
                      name:"p1",
                      description:"p-value threshold",
                      defaultValue:"1.0, 0.5, 0.05, 5 × 10−4, 5 × 10−6, and 5 × 10−8",
                      
                      setValue:null,
                      errorFlag:false,
                      
                  },
                  {
                      id:"002",
                      name:"r^2",
                      description:"r2 threshold",
                      defaultValue:"0.2, 0.4, 0.6, 0.8",
                      
                      setValue:null,
                      errorFlag:false,
                      
                  }
                ]
            },
            {
                id :5,
                name:"JAMPred",
                toggleName:null,
                summary:"JAMPred is a method obviates a practical need to define LD blocks as required, and provides the flexibility to capture highly complex genetic models.<br>Doi: 10.1002/gepi.22245",
                parameters:[
                  {
                      id:"001",
                      name:"kb",
                      description:"clump kb radius",
                      defaultValue:"250",
                      
                      setValue:null,
                      errorFlag:false,
                      
                  },
                  {
                      id:"002",
                      name:"r^2",
                      description:"r2 threshold",
                      defaultValue:"0.2, 0.4, 0.6, 0.8",
                      
                      setValue:null,
                      errorFlag:false,
                      
                  },
                  {
                      id:"003",
                      name:"kb",
                      description:"clump kb radius",
                      defaultValue:"250",
                      
                      setValue:null,
                      errorFlag:false,
                      
                  }
                ]
            },
            {
                id :6,
                name:"SBayesR",
                toggleName:null,
                summary:"SBayesR performs Bayesian posterior inference through the combination of a likelihood that connects the multiple regression coefficients with summary statistics from GWAS and a finite mixture of normal distributions prior on the marker effects.<br>Doi: 10.1038/s41467-019-12653-0",
                parameters:[
                  {
                      id:"001",
                      name:"p1",
                      description:"p-value threshold",
                      defaultValue:"1.0, 0.5, 0.05, 5 × 10−4, 5 × 10−6, and 5 × 10−8",
                      
                      setValue:null,
                      errorFlag:false,
                      
                  }
                ]
            },
            {
                id :7,
                name:"DBSLMM",
                toggleName:null,
                summary:"Deterministic Bayesian Sparse Linear Mixed Model (DBSLMM) relies on a flexible modeling assumption on the effect size distribution, and also relies on a simple deterministic search algorithm to yield an approximate analytic estimation solution using summary statistics only.<br>Doi: 10.1016/j.ajhg.2020.03.013",
                parameters:[
                  {
                      id:"001",
                      name:"p1",
                      description:"p-value threshold",
                      defaultValue:"1.0, 0.5, 0.05, 5 × 10−4, 5 × 10−6, and 5 × 10−8",
                      
                      setValue:null,
                      errorFlag:false,
                      
                  },
                  {
                      id:"003",
                      name:"kb",
                      description:"clump kb radius",
                      defaultValue:"250",
                      
                      setValue:null,
                      errorFlag:false,
                      
                  }
                ]
            },
            {
                id :8,
                name:"SDPR",
                toggleName:null,
                summary:"SDPR connects the marginal coefficients in summary statistics with true effect sizes through Bayesian multiple Dirichlet process regression.<br>Doi: 10.1371/journal.pgen.1009697",
                parameters:[
                  {
                      id:"001",
                      name:"p1",
                      description:"p-value threshold",
                      defaultValue:"1.0, 0.5, 0.05, 5 × 10−4, 5 × 10−6, and 5 × 10−8",
                      
                      setValue:null,
                      errorFlag:false,
                      
                  }
                ]
            },
            {
                id :9,
                name:"AnnoPred",
                toggleName:null,
                summary:"Anno-Pred leverages diverse types of genomic and epigenomic functional annotations in genetic risk prediction for complex diseases.<br>Doi: 10.1371/journal.pcbi.1005589",
                parameters:[
                  {
                      id:"001",
                      name:"p1",
                      description:"p-value threshold",
                      defaultValue:"1.0, 0.5, 0.05, 5 × 10−4, 5 × 10−6, and 5 × 10−8",
                      
                      setValue:null,
                      errorFlag:false,
                      
                  }
                ]
            },
            {
                id :10,
                name:"SBLUP",
                toggleName:null,
                summary:"SBLUP converts the least-squares SNP estimates into approximate best linear unbiased predictors.<br>Doi: 10.1038/s41562-016-0016",
                parameters:[
                  {
                      id:"001",
                      name:"p1",
                      description:"p-value threshold",
                      defaultValue:"1.0, 0.5, 0.05, 5 × 10−4, 5 × 10−6, and 5 × 10−8",
                      
                      setValue:null,
                      errorFlag:false,
                      
                  }
                ]
            }
          ],
          

        },
        //正在配置参数的算法id
        settingPId:null,
        //参数弹窗展示的参数数据
        showParameters:[],
        //是否展示参数配置弹窗
        showParModal:false,
        //提交按钮配置弹窗
        subModalToggle:'modal'
      }
    },
    computed:{
      //用户设置的参数
			algorithmsData(){
				return this.$store.getters['algorithmsData/getAlgorithmsData']
			},
      //提交按钮是否可点
      submitFlag(){
        return this.algorithmsData.length > 0 ? false:true
      },
      //上传文件标志
     fileData(){
        return this.$store.getters['uploadFileData/getFileData']
      }
    },
    methods: {
      //是否选择了算法
      algorithmChecked(e,id){
        //算法参数设置弹窗展示flag=true
        if(!this.showParModal){
          this.showParModal = true
        }
        this.settingPId = id
        this.algorithms.algorithmsShow.forEach((algorithm)=>{
            if(algorithm.id === id){
                if(e.target.checked ) {
                    //设置弹窗
                  algorithm.toggleName = "modal"
                  //为弹窗设置对应算法的参数
                  this.showParameters=algorithm.parameters
                  this.setAlgorithmsData(id)
                }else{
                    //取消弹窗
                    algorithm.toggleName = null
                    //删除配置的算法参数
                    this.$store.dispatch("algorithmsData/deleteAlgorithmsData",id)
                }
            } 
        })
         
      },
      //改变showParameters值
      changeShowParameters(id){
        //算法参数设置弹窗展示flag=true
        if(!this.showParModal) this.showParModal = true

        this.algorithms.algorithmsShow.forEach((algorithm)=>{
            if(algorithm.toggleName === "modal" && algorithm.id === id){
                //为弹窗设置对应算法的参数
                this.showParameters=algorithm.parameters
            } 
        })
      },
      //设置弹窗展示flag
      changeShowParModal(){
        this.showParModal = !this.showParModal
      },
      //点击提交按钮
      clickSubmit(){
        this.showParModal = false
      },
      //设置提交参数algorithmsData
      setAlgorithmsData(settingPId){
        //定义添加的数据参数
        let algorithmAdd = {}
        this.algorithms.algorithmsShow.forEach(function(algorithm){
          //组装参数
          if(algorithm.id === settingPId){
            algorithmAdd['id'] = algorithm['id']
            algorithmAdd['name'] = algorithm['name']
            let parameters = []
            algorithm['parameters'].forEach(function(parameter){
                let newParameter = {}
                newParameter['id'] = parameter['id']
                newParameter['name'] = parameter['name']
                newParameter['value'] = parameter['setValue'] === null  ? parameter['defaultValue'] : parameter['setValue']
                parameters.push(newParameter)
            })
            algorithmAdd['parameters'] = parameters

          } 
          return false

        })
        this.$store.dispatch('algorithmsData/addAlgorithmsData',algorithmAdd)
      },
      //数据提交
      dataSubmit(){
        if(!this.fileData.uploadFlag){//未上传文件
          //提示框
          this.$MessageBox.alert('Please upload GWAS summary statistics !', 'prompt', {
            confirmButtonText: 'OK',
            // callback: action => {
            //   if("confirm" === action){//点击确定
            //   }
            // }
          })
          return
        }

        //提交参数
      }
    },
    mounted() {
      //为总线绑定函数
      this.$bus.$on('algorithmChecked',this.algorithmChecked)
      this.$bus.$on('changeShowParModal',this.changeShowParModal)
      this.$bus.$on('changeShowParameters',this.changeShowParameters)
      //获取算法数据
      axios.get("/algorithms/getAlgorithmsInfo",
        {
          params:{
          }
        }
      ).then((response) => {
          const data = response.data 
          if(!isEmpty(data)){
              const resultMap = data.data
              const code = resultMap.code
              const algorithmsArr = resultMap.data
              //解析后台返回的算法参数
              if(code === 0 && !isEmpty(algorithmsArr)){
                let algorithmsArrShow = [];
                for(let i=0 ; i< algorithmsArr.length ; i++){
                  let algorithms = algorithmsArr[i]
                  algorithms["toggleName"] = null
                  let parameterArr = algorithms.parameters
                  let parametersArrShow = [];
                  if(!isEmpty(parameterArr) && parameterArr.length>0){
                    for(let j=0 ; j< parameterArr.length ; j++){
                      let parameter = parameterArr[j]
                      parameter["setValue"] = null
                      parameter["errorFlag"] = false
                      parametersArrShow.push(parameter)
                    }
                  }
                  algorithms["parameters"] = parametersArrShow
                  algorithmsArrShow.push(algorithms)
                }
                //为页面显示的参数赋值
                this.algorithms.algorithmsShow = algorithmsArrShow
              }
          }else{
            //跳转错误页面
          }
        })
    },
    beforeDestroy() {
      //销毁总线绑定的函数
      this.$bus.$off('algorithmChecked')
      this.$bus.$off('changeShowParModal')
      this.$bus.$off('changeShowParameters')
    },
}
</script>
<style>
.algorithm{
  border-bottom: 1px solid #c9c9d2;
}
.parameters{
    margin-left: 0.5rem;
    padding-bottom: 0.5rem;
}
</style>