<template>
<div class="container-fluid max-w-screen-md vstack gap-6" style="margin-top: 4rem;">
   <vue-element-loading :active="loading" :is-full-screen="true" spinner="ring"/>
          <!-- Profile picture -->
          <div class="card">
            <div class="card-body">
              <div class="d-flex align-items-center">
                <div>
                  <div class="d-flex align-items-center">
                    <div class="avatar avatar-lg bg-warning rounded-circle text-white" style="background-color: #796CFF !important;">
                      <svg data-v-72a142e8="" t="1646979744016" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="11215" width="200" height="200" class="login-icon"><path data-v-72a142e8="" d="M512 0A512 512 0 1 0 1024 512 512 512 0 0 0 512 0z m0 34.133333a477.476571 477.476571 0 0 1 359.862857 791.747048c-1.852952-5.36380999-3.657143-10.727619-5.753905-15.993905a377.465905 377.465905 0 0 0-198.70476199-204.312381 4.583619 4.583619 0 0 1-0.731428-0.292571 213.333333 213.333333 0 1 0-309.345524 0l-1.31657101 0.536381a376.88076201 376.88076201 0 0 0-198.119619 204.01980899 312.417524 312.417524 0 0 0-5.753905 15.99390501A477.476571 477.476571 0 0 1 512 34.133333z m119.466667 557.68990501a179.2 179.2 0 0 1-298.666667-133.12 179.2 179.2 0 1 1 298.666667 133.12z m-452.705524 262.241524c1.26781001-4.437333 2.438095-8.923429 3.803428-13.16571401A344.746667 344.746667 0 0 1 385.219048 629.955048a212.163048 212.163048 0 0 0 253.561904 0 343.82019001 343.82019001 0 0 1 202.70323799 210.944c1.365333 4.193524 2.535619 8.728381 3.75466701 13.16571399a476.696381 476.696381 0 0 1-666.428952 0z m0 0" p-id="11216" fill="#ffffff"></path></svg>
                    </div>
                    <div class="ms-4">
                      <span class="h4 d-block mb-0">{{firstName}} {{lastName}}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div>
            <div class="mb-5">
              <h4>Contact Information</h4>
            </div>
            <form>
              <div class="row g-5">
                <div class="col-md-6 col-sm-6">
                  <div>
                    <label class="form-label">First name</label>
                    <input type="text" class="form-control"  ref="firstName" @blur='checkName'  v-model.trim="firstName" >
                  </div>
                </div>
                <div class="col-md-6 col-sm-6">
                  <div>
                    <label class="form-label">Last name</label>
                    <input type="text" class="form-control" ref="lastName" @blur='checkName'  v-model.trim="lastName">
                  </div>
                </div>
                <div class="col-md-12">
                  <div>
                    <label class="form-label">Mobile number</label>
                    <input type="tel" class="form-control" v-model.trim="mobile" @blur="checkMobile" ref="mobile">
                    <p class="errrorMsg" v-if="errorMobile" >This email address is not valid.</p>
                  </div>
                </div>
                <div class="col-md-12">
                  <div>
                    <label class="form-label" for="email">Job Title</label>
                    <input type="text" class="form-control" ref="jobTitle" v-model.trim="jobTitle" >
                  </div>
                </div>
                <div class="col-12">
                  <div>
                    <label class="form-label">Organisation</label>
                    <input type="text" class="form-control" ref="organisation" v-model.trim="organisation"  >
                  </div>
                </div>
                <div class="col-md-6">
                  <div>
                    <label class="form-label">City</label>
                    <input type="text" class="form-control" v-model.trim="city">
                  </div>
                </div>
                <div class="col-md-6">
                  <div>
                    <label class="form-label">Country</label>
                    <input type="text" class="form-control" v-model.trim="country">
                  </div>
                </div>
                <div class="col-12 text-end">
                  <button type="submit" class="btn btn-sm btn-primary" @click="updatedUser">Save</button>
                </div>
              </div>
            </form>
          </div>
          <hr class="my-6">
          <!-- Individual switch cards -->
          <div class="row g-6">
            <div class="col-md-12">
              <div class="card">
                <div class="card-body">
                  <h4 class="text-danger mb-2">Deactivate account</h4>
                  <p class="text-sm text-muted mb-4">
                    Permanently remove your account and all of its contents. This action is not reversible – please be certain.
                  </p>
                  <button type="button" class="btn btn-sm btn-danger">Delete my account</button>
                </div>
              </div>
            </div>
          </div>
        </div>
</template>

<script>
import {Account} from "@/api"
import {checkMobile,isEmpty,checkName }  from "@/utils/validate"

export default {
    name:"SettingsItem",
    data () {
      return {
        userId:"",
        firstName:"",
        lastName:"",
        mobile:"",
        jobTitle:"",
        organisation:"",
        city:"",
        country:"",
        agree:false,
        errorName:false,
        errorMobile:false,
        loading:false

      }
    },
    computed: {
      disabledVal(){
        return !this.agree;
      }
    },
    methods: {
      //注册
      updatedUser(){
        //校验数据
        if(isEmpty(this.firstName)||this.errorName){
          alert("Please enter or modify First Name!")
          this.$refs.firstName.focus()
          return;
        }
        if(isEmpty(this.lastName)||this.errorName){
          alert("Please enter or modify Last Name!")
          this.$refs.lastName.focus()
          return;
        }
        if(isEmpty(this.mobile)|| this.errorMobile){
          alert("Please enter or mobile email address!")
          this.$refs.mobile.focus()
          return;
        }
        if(isEmpty(this.jobTitle)){
          alert("Please enter or modify Job Title!")
          this.$refs.jobTitle.focus()
          return;
        }
        if(isEmpty(this.organisation)){
          alert("Please enter or modify organisation!")
          this.$refs.organisation.focus()
          return;
        }
        if(isEmpty(this.city)){
          alert("Please enter or modify city!")
          this.$refs.city.focus()
          return;
        }
        if(isEmpty(this.country)){
          alert("Please enter or modify country!")
          this.$refs.country.focus()
          return;
        }
        //加载中
        this.loading=true
        //组装数据
        let subData = {
          id:this.userId,
          firstName:this.firstName,
          lastName:this.lastName,
          jobTitle:this.jobTitle,
          city:this.city,
          country:this.country,
          organisation:this.organisation,
          mobile:this.mobile
        }

        //提交数据
        Account.updatedUser(subData).then((response) => {
          const data = response
          if(!isEmpty(data)){
              const resultMap = data.data
              const code = resultMap.code
              const msg = resultMap.msg
              if(code === 0){
                //关闭加载中
                this.loading=false
                //提示框
                this.$MessageBox.alert('modify successfully !', 'prompt', {
                  confirmButtonText: 'OK',
                })
              }else if (code === 4){
                //关闭加载中
                this.loading=false
                //提示框
                this.$MessageBox.alert(msg, 'prompt', {
                  confirmButtonText: 'OK',
                  callback: () => {
                    this.$refs.email.focus()
                  }
                })
              }else{
                //todo 跳转错误页面
                //关闭加载中
                this.loading=false
                //提示框
                this.$MessageBox.alert('The system is busy. Please try again later', 'prompt', {
                  confirmButtonText: 'OK'
                })
              }
          }else{
            //todo 跳转错误页面
            //关闭加载中
            this.loading=false
            //提示框
            this.$MessageBox.alert('The system is busy. Please try again later', 'prompt', {
              confirmButtonText: 'OK'
            })
          }
        })
      },
      //校验姓名
      checkName(){
        if(!isEmpty(this.name)){
          this.errorName=checkName(this.name)
        }
      },
      //校验手机号
      checkMobile(){
        if(!isEmpty(this.mobile)){
          this.errorMobile=checkMobile(this.mobile)
        }
      },
      initUserInfo(){
         //初始化用户数据
        Account.getUserInfo().then(
        (response) => {
          const code = response.code
          if(code==="400"){
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
          const data = response.data 
          if(!isEmpty(data)){
              const user = data
              if(!isEmpty(user)){
                this.userId=user.id
                this.firstName=user.firstName 
                this.lastName=user.lastName
                this.jobTitle=user.jobTitle
                this.city=user.city
                this.country=user.country
                this.organisation=user.organisation
                this.mobile=user.mobile
              }
          }        
        })
      }
    },
    mounted () {
     this.initUserInfo()
    }
}
</script>

<style>
.errrorMsg{
    color: #e30404;
    font-size: x-small;
}
.eyes{
  height: 1.5rem;
}
.outer{
    background-color: #fff;
    border: 1px solid #e7eaf0;
    border-radius: 0.375rem;
    box-shadow: 0 1px 2px rgb(50 50 71 / 8%);
    color: #16192c;
    display: block;
}
.inner{
    border: 0px solid #ffffff !important;
    border-radius: 0.375rem !important;
    box-shadow: 0 0px 0px rgb(255 255 255) !important;
    display: inline-block !important;
}
.inlinediv{
  display: inline-block !important;
}
.mr{
  margin-right: 0.25rem;
}
.ml{
  margin-left: 0.25rem;
}
.asterisk{
  width: 0.5rem;
}
</style>