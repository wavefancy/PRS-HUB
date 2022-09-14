<template>
    <!-- Content -->
    <div class="flex-lg-1 h-screen overflow-y-lg-auto">
      <vue-element-loading :active="loading" :is-full-screen="true" spinner="ring"/>
      <!-- Navbar -->
      <header class="position-sticky top-0 overlap-10 bg-surface-primary border-bottom">
        <div class="container-fluid py-4">
          <div class="row align-items-center">
            <div class="col">
              <div class="d-flex align-items-center gap-4">
                
                <h1 class="h4 ls-tight">Setup</h1>
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
              <div class="list-unstyled mb-4 mle" v-for="parameter in showParameters" :key="parameter.id">
                <p class="py-1 d-flex align-items-center">
                  {{ parameter.name }}:{{ parameter.description }}
                </p>
                <div class="input-group input-group-sm input-group-inline">
                  <input
                    type="text"
                    class="form-control"
                    v-model.trim="parameter.setValue"
                    :placeholder='"Default:"+parameter.defaultValue'
                  />
                </div>
                <p v-show="parameter.errorFlag" class="errorMsg" name="errorMsg">
                  Please enter the correct data in the default format!
                </p>
              </div>
              <div class="modal-footer" style="border-top: 0px solid #e7eaf0;">
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
            <div class="col-md-12">
              <div>
                <h6>Job Name</h6>
                <input type="text" class="form-control" ref="jobName" v-model.trim="jobName" placeholder="Your Job Name">
              </div>
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
         <div class="modal fade" id="discription"  ref="discription"
          tabindex="-1" aria-labelledby="discription" aria-hidden="true"   >
          <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
              <i class="bi bi-x-lg off-x" data-bs-dismiss="modal" ></i>
              <div class="modal-body px-12" style="padding-top: 0.5rem;" >
                  <p class="text-sm text-muted" v-html="showDiscription"></p>
              </div>
            </div>
          </div>
        </div>
        <div class="container-fluid vstack gap-10">
          <div class="card">
            <div class="card-body">
              <div class="">
                <GwasPanel></GwasPanel>
              </div>
              <div class="rp">
                <ReferencePanel></ReferencePanel>
              </div>
              <div class="modal-footer" style="border-top: 0px solid #e7eaf0; align-items: center;justify-content: center;">
                <button type="button" class="btn btn-sm btn-primary" @click="addPlan">
                  Confirm
                </button>
              </div>
            </div>
          </div>
          
          <div class="card">
            <div class="card-body">
              <h4 class="mb-3">Selected</h4>
              <div class="row g-6 mt-n3">
                <div class="col-xxl-12 col-md-12 col-sm-12" v-for="(plan ,index) in planVals" :key="index">
                  <!-- <div class="position-relative d-flex align-items-center"> -->
                    <div class="row" v-if="index%2 === 0">
                       <p class="plan"  style="color:#f56c6c;" >{{plan[0]}}</p>  <p class="plan"  style="color:#f56c6c;" >{{plan[1]}} </p><i class="bi bi-x-lg off-x" style="width:1rem;" data-bs-dismiss="modal" @click="rmPlan(index)" ></i>
                    </div>
                    <div class="row" v-if="index%2 === 1" >
                       <p class="plan" style="color: #67c23a;">{{plan[0]}}</p>  <p class="plan" style="color: #67c23a;">{{plan[1]}} </p><i class="bi bi-x-lg off-x" style="width:1rem;" data-bs-dismiss="modal" @click="rmPlan(index)" ></i>
                    </div>
                    <!-- <div class="me-4">
                      <div class="avatar rounded-circle border-2 border-dashed">
                        <i class="bi bi-plus-lg"></i>
                      </div>
                    </div> -->
                  <!-- </div> -->
                </div>
              </div>
            </div>
          </div>
          <AlgorithmsItem :algorithmsShow='algorithms.algorithmsShow' ></AlgorithmsItem>
        </div>
      </main>
    </div>
</template>
<script>
import {Prs} from "@/api"
import { isEmpty }  from "@/utils/validate"
// import {mapActions,mapGetters} from 'vuex'
// import UploadFile from "@/components/commons/UploadFile"
import PopModal from "@/components/commons/PopModal"
import AlgorithmsItem from "@/pages/integrations/AlgorithmsItem"
import ReferencePanel from "@/pages/integrations/ReferencePanel"
import GwasPanel from "@/pages/integrations/GwasPanel"

export default {
    name:"MultipleAncestriesItem",
    components:{
      PopModal,
      GwasPanel,
      AlgorithmsItem,
      ReferencePanel
    },
    data() {
      return {
        algorithms:{
          //算法参数-页面展示
          algorithmsShow:[]
        },
        //选择的gwas上传文件id
        gwasFileId:null,
        gwasFileName:"",
        //参考
        referencePanel:"",
        //正在配置参数的算法id
        settingPId:null,
        //参数弹窗展示的参数数据
        showParameters:[],
        //是否展示参数配置弹窗
        showParModal:false,
        //提交按钮配置弹窗
        subModalToggle:'modal',
        //算法的介绍
        showDiscription:'',
        loading:false,
        //工作名称
        jobName:"",
        planVals:[]
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
      //改变showDiscription值
      changeShowDiscription(val){
        this.showDiscription=val
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
        if(isEmpty(this.gwasFileId)){//未上传文件
          //提示框
          this.$MessageBox.alert('Please Select GWAS summary statistics !', 'Message', {
            confirmButtonText: 'OK',
          })
          return
        }
        if(isEmpty(this.referencePanel)){
          //提示框
          this.$MessageBox.alert('Please Select LD reference panel !', 'Message', {
            confirmButtonText: 'OK',
          })
          return
        }
        let subData = {
          algorithmList:this.algorithmsData,
          fileGWASId:this.gwasFileId,
          multipleFileIds:this.planVals,
          headers: {'accessToken':  localStorage.getItem("accessToken")}
        }
        //加载中
        this.loading=true
        //提交参数
        Prs.setParametersInfo(subData).then((response) => {
          const data = response;
          if(data.code === 0){
            const resData = data.data;
            if(resData.code === 0){
              //关闭加载中
                this.loading=false
                //提示框
                this.$MessageBox.alert('Submit successfully .', 'Message', {
                  confirmButtonText: 'OK',
                  callback: () => {
                   this.toStatistics()
                  }
                })
            }
          }else  if(data.code==="400"){
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
      //跳转到登录页
      toStatistics(){
        this.$router.push({
					name:'statistics'
				});
      },
      //
      referenceSelect(val){
        this.referencePanel=val;
      },
      //选择的gwasfile的id
      gwasPanelSelect(val,nameVal){
        this.gwasFileId=val;
        this.gwasFileName=nameVal;
      },
      addPlan(){
        let plan = null
        if(isEmpty(this.gwasFileId)){//未上传文件
          //提示框
          this.$MessageBox.alert('Please Select GWAS summary statistics !', 'Message', {
            confirmButtonText: 'OK',
          })
          return
        }
        if(isEmpty(this.referencePanel)){
          //提示框
          this.$MessageBox.alert('Please Select LD reference panel !', 'Message', {
            confirmButtonText: 'OK',
          })
          return
        }

        plan = this.gwasFileName+"+"+this.referencePanel
        let planArr=[];
        planArr.push(this.gwasFileName)
        planArr.push(this.referencePanel)
        if(this.planVals.length===0){
          planArr.push("color:#f56c6c;")
          this.planVals.push(planArr)
        }else{
          let addFlag = true
          for(let i = 0 ;i<this.planVals.length;i++){
              if(plan === this.planVals[i][0]+"+"+this.planVals[i][1]){
                addFlag = false
              }
          }
          if(addFlag){
            if(this.planVals.length%2 === 0){
              planArr.push("color:#f56c6c;")
            }else{
              planArr.push("color: #67c23a;")
            }
            this.planVals.push(planArr)
          }
        }
      },
      rmPlan(i){
        this.planVals.splice(i,1)
      }
    },
    mounted() {
      
      this.$store.dispatch("algorithmsData/deleteAllAlgorithmsData")
      //为总线绑定函数
      this.$bus.$on('algorithmChecked',this.algorithmChecked)
      this.$bus.$on('changeShowParModal',this.changeShowParModal)
      this.$bus.$on('changeShowParameters',this.changeShowParameters)
      this.$bus.$on('changeShowDiscription',this.changeShowDiscription) 
      this.$bus.$on('referenceSelect',this.referenceSelect)
      this.$bus.$on('gwasPanelSelect',this.gwasPanelSelect)
      //获取算法数据
      let subData = {
        type:"multiple"
      }
      Prs.getAlgorithmsInfo(subData).then((response) => {
        const code = response.code
        if(code==="400"){
          //跳转登录页面
          //提示框
          // this.$MessageBox.alert("The login status is invalid, please log in again !", 'Message', {
          //   confirmButtonText: 'OK',
          //   callback: () => {
          //     //跳转登录页面
          //     this.$router.push({
          //       name:'login'
          //     });
          //   }
          // })
          return
        }
        const data = response.data 
        if(!isEmpty(data)){
            const algorithmsArr = data.data
            const code = data.code
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
      this.$bus.$off('changeShowDiscription')
      this.$bus.$off('referenceSelect')
      this.$bus.$off('gwasPanelSelect')
    }
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
.mle{
  margin-left: 1rem;
}
.off-x{
  text-align: right;
    padding-right: 0.3rem;
}
.rp{
  margin-top: 2rem;
}
.plan{
  width: auto;
}
</style>