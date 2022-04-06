<template>
<div class="outer">
  <vue-element-loading :active="loading" :is-full-screen="true" spinner="ring"/>
  <div class="px-5 py-5 p-lg-0 h-screen bg-surface-secondary d-flex flex-column justify-content-center">
    <div class="d-flex justify-content-center">
      <div class="col-12 col-md-9 col-lg-6 min-h-lg-screen d-flex flex-column justify-content-center py-lg-16 px-lg-20 position-relative">
        <div class="row">
          <div class="col-lg-10 col-md-9 col-xl-7 mx-auto">
            <div class="text-center mb-12">
              <a class="navbar-brand py-lg-2 mb-lg-5 px-lg-6 me-0" href="#" style="color:#796CFF">
                <img style="height: 2.875rem;"  :src="imgUrls.primary"  alt="...">PRS-hub
              </a>
              <h1 class="ls-tight font-bolder mt-6">
                Create your account
              </h1>
              <p class="mt-2">It's free and easy</p>
            </div>
            <div class="mb-2">
              <div class="inlinediv col-md-6">
                <div class="mr">
                  <label class="form-label">First name<img class="asterisk" :src="imgUrls.asterisk" > </label>
                  <input type="text" class="form-control" ref="firstName" @blur='checkName'  v-model.trim="firstName" placeholder="Your First name">
                </div>
              </div>
              <div class="inlinediv col-md-6">
                <div class="ml">
                  <label class="form-label">Last name<img class="asterisk" :src="imgUrls.asterisk" > </label>
                  <input type="text" class="form-control" ref="lastName" @blur='checkName'  v-model.trim="lastName" placeholder="Your Last name">
                </div>
              </div>
              <!-- <label class="form-label" for="name">Name <img class="asterisk" :src="imgUrls.asterisk" > </label>
              <input type="text" class="form-control" ref="name" @blur='checkName'  v-model.trim="name" placeholder="Your name"> -->
              <p class="errrorMsg" v-if='errorName' >Are you sure you entered your name correctly?</p>
            </div>
            <div class="mb-2">
              <label class="form-label" for="email">Email address <img class="asterisk" :src="imgUrls.asterisk" > </label>
              <input type="email" class="form-control" ref="email" @blur="checkEmail"  v-model.trim="email"  placeholder="Your email address">
              <p class="errrorMsg" v-if="errorEmail" >This email address is not valid.</p>
            </div>
            <div class="mb-2">
              <label class="form-label" >Job Title <img class="asterisk" :src="imgUrls.asterisk" > </label>
              <input type="text" class="form-control" ref="jobTitle" v-model.trim="jobTitle"  placeholder="Your Job Title address">
            </div>
            <div class="mb-2">
              <label class="form-label" >Organisation <img class="asterisk" :src="imgUrls.asterisk" > </label>
              <input type="text" class="form-control" ref="organisation" v-model.trim="organisation"  placeholder="Your Organisation address">
            </div>
            <div  class="mb-2">
              <div class="inlinediv col-md-6">
                <div class="mr">
                  <label class="form-label">City<img class="asterisk" :src="imgUrls.asterisk" > </label>
                  <input type="text" class="form-control"  v-model.trim="city" placeholder="Your City">
                </div>
              </div>
              <div class="inlinediv col-md-6">
                <div class="ml">
                  <label class="form-label">Country<img class="asterisk" :src="imgUrls.asterisk" > </label>
                  <input type="text" class="form-control"  v-model.trim="country" placeholder="Your Country">
                </div>
              </div>
            </div>
            <div class="mb-2">
              <label class="form-label" for="password">Password<img class="asterisk" :src="imgUrls.asterisk" > </label>
              <div class="outer">
                <input :type="passwordType" class="inner form-control" style="width: 90%;" ref="password" @blur="checkPassword" v-model.trim="password" placeholder="Password" autocomplete="current-password">
                <img class="eyes" :src="eyesImage" @click="clickEyes">
              </div>
              <p class="errrorMsg" v-if="errorPw">Incorrect password format</p>
              <p style="font-size: xx-small;color: rgba(107,123,147);">Use 6 ~ 20 characters with a mix of uppercase and lowercase letters, Numbers, and symbols.</p>
            </div>
            <div class="mb-2">
              <label class="form-label" for="password">Confirm<img class="asterisk" :src="imgUrls.asterisk" > </label>
              <div class="outer">
                <input :type="passwordType" class="inner form-control" style="width: 90%;" ref="confirm" @blur="confirmPw" v-model.trim="confirm" placeholder="Confirm" autocomplete="current-password">
                <img class="eyes" :src="eyesImage" @click="clickEyes"/>
              </div>
              <p class="errrorMsg" v-if="errorCon">Different passwords</p>
            </div>
            <div class="mb-2">
              <div class="form-check">
                <input class="form-check-input" type="checkbox" v-model="agree" name="check_example" id="check-remind-me">
                <label class="form-check-label font-semibold text-muted" for="check-remind-me">
                  By creating an account means you agree to the Terms and Conditions, and our Privacy Policy
                </label>
              </div>
            </div>
            <div>
              <button class="btn btn-primary w-full" @click="register"  v-bind:disabled="disabledVal">
                Register
              </button>
            </div>
            <div class="my-6">
              <small>Already have an account?</small>
              <button  @click="signIn" class="text-warning text-sm font-semibold">Sign in</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
</template>

<script>
import {Account} from "@/api"
import { checkEmail,checkPassword,isEmpty,checkName }  from "@/utils/validate"

export default {
    name:"RegisterItem",
    data () {
      return {
        imgUrls:{
          primary:"./img/logos/PRS-hub.png",
          asterisk:"./img/asterisk.png",
          prompt:"./img/prompt.png"
        },
        firstName:"",
        lastName:"",
        email:"",
        password:"",
        confirm:"",
        jobTitle:"",
        organisation:"",
        city:"",
        country:"",
        eyesImage:"./img/logos/close.png",
        passwordType:"password",
        agree:false,
        errorName:false,
        errorEmail:false,
        errorPw:false,
        errorCon:false,
        loading:false,
        eyesType:false

      }
    },
    computed: {
      disabledVal(){
        return !this.agree;
      }
    },
    methods: {
      //注册
      register(){
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
        if(isEmpty(this.email)|| this.errorEmail){
          alert("Please enter or modify email address!")
          this.$refs.email.focus()
          return;
        }
        if(isEmpty(this.password)|| this.errorPw){
          alert("Please enter or modify password!")
          this.$refs.password.focus()
          return;
        }
        if(isEmpty(this.confirm)|| this.errorCon){
          alert("Please confirm password!")
          this.$refs.confirm.focus()
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
          firstName:this.firstName,
          lastName:this.lastName,
          jobTitle:this.jobTitle,
          city:this.city,
          country:this.country,
          organisation:this.organisation,
          email:this.email,
          password:this.password
        }

        //提交数据
        Account.auth(subData).then((response) => {
          const data = response
          if(!isEmpty(data)){
              const resultMap = data.data
              const code = resultMap.code
              const msg = resultMap.msg
              if(code === 0){
                //关闭加载中
                this.loading=false
                //提示框
                this.$MessageBox.alert('Click OK to jump to the login page', 'prompt', {
                  confirmButtonText: 'OK',
                  callback: action => {
                    if("confirm" === action){//点击确定
                      //注册成功跳转登录页面
                      this.signIn()
                    }
                  }
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
      //跳转到登录页
      signIn(){
        this.$router.push({
					name:'login',
					query:{
						email:this.email,
            password:this.password
					}
				});
      },
      //校验姓名
      checkName(){
        if(!isEmpty(this.name)){
          this.errorName=checkName(this.name)
        }
      },
      //校验邮箱
      checkEmail(){
        if(!isEmpty(this.email)){
          this.errorEmail=checkEmail(this.email)
        }
      },
      //校验密码
      checkPassword(){
         if(!isEmpty(this.password)){
           //8位以上的字母与数字组合
           this.errorPw=checkPassword(this.password)
         }
      },
      //确认密码
      confirmPw(){
        if(!isEmpty(this.password)&&!isEmpty(this.confirm)){
          if(this.password != this.confirm){
            this.errorCon=true
          }else{
            this.errorCon=false
          }
        }
      },
      clickEyes(){
        this.eyesType = !this.eyesType
        if(this.eyesType){
          this.eyesImage = "./img/logos/open.png"
          this.passwordType = "text"
        }else{
          this.eyesImage = "./img/logos/close.png"
          this.passwordType = "password"
        }
      }
    },
    mounted () {
      console.log(this.$store.state.vueElementLoading)
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