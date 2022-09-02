<template>
  <div class="px-5 py-5 p-lg-0 h-screen bg-surface-secondary d-flex flex-column justify-content-center">
  <vue-element-loading :active="loading" :is-full-screen="true" spinner="ring"/>
      <div class="d-flex justify-content-center">
        <div class="col-12 col-md-9 col-lg-6 min-h-lg-screen d-flex flex-column justify-content-center py-lg-16 px-lg-20 position-relative">
          <div class="row">
            <div class="col-lg-10 col-md-9 col-xl-7 mx-auto">
              <div class="text-center mb-12">
                <a class="navbar-brand py-lg-2 mb-lg-5 px-lg-6 me-0" href="#" style="color:#796CFF">
                  <img style="height: 2.875rem;"  :src="imgUrls.primary"  alt="...">PRS-hub
                </a>
                <h1 class="ls-tight font-bolder mt-6">
                  Welcome back!
                </h1>
                <p class="mt-2">Let's build someting great</p>
              </div>
              <div class="mb-5">
                <label class="form-label" for="email">Email address</label>
                <input type="email" class="form-control" ref="email" v-model="email" placeholder="Your email address" @keyup.enter="clickEnterKey('email')">
              </div>
              <div class="mb-5">
                <div class="d-flex align-items-center justify-content-between">
                  <div>
                    <label class="form-label" for="password" >Password</label>
                  </div>
                  <!-- <div class="mb-2">
                    <a href="./basic-recover.html" class="text-sm text-muted text-primary-hover text-underline">Forgot password?</a>
                  </div> -->
                </div>
                <input type="password" class="form-control" ref="password" v-model="password" placeholder="Password" autocomplete="current-password"  @keyup.enter="clickEnterKey('password')">
              </div>
              <!-- <div class="mb-5">
                <div class="form-check">
                  <input class="form-check-input" type="checkbox" v-model="logged" name="check_example" id="check-remind-me">
                  <label class="form-check-label" for="check-remind-me">
                    Keep me logged in
                  </label>
                </div>
              </div> -->
              <div>
                <button class="btn btn-primary w-full" @click="login">
                  Sign in
                </button>
              </div>
              
              <div class="my-6">
                <small>Don't have an account?</small>
                <router-link  to="/register" class="text-warning text-sm font-semibold">Sign up</router-link>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
</template>

<script>
import {Account} from "@/api"
import { isEmpty }  from "@/utils/validate"

export default {
    name:"LoginItem",
    data () {
      return {
        imgUrls:{
          primary:"./img/logos/PRS-hub.png"
        },
        email:"",
        password:"",
        logged:false,
        loading:false
      }
    },
    methods: {
      clickEnterKey(ref){
        if("password" === ref){
          if(!isEmpty(this.password)){
            this.login()
          }
        }else{
          if(!isEmpty(this.email)){
            this.$refs.password.focus()
          }
        }
      },
      login(){
        if(isEmpty(this.email)){
          alert("Please enter email address!")
          this.$refs.email.focus()
          return;
        }
        if(isEmpty(this.password)){
          alert("Please enter password!")
          this.$refs.password.focus()
          return;
        }
         //加载中
        this.loading=true
        //组装数据
        let subData = {
          email:this.email,
          password:this.password
        }

        //提交数据
        Account.reqLogin(subData).then((res) => {
          const data = res.data
          if(!isEmpty(data)){
              const code = data.code
              const msg = data.msg
              const token = data.token
              if(code === 0){
                //关闭加载中
                this.loading=false
                //存储token
                localStorage.setItem('accessToken',token)
                this.$store.dispatch('loginData/setLoginData',{accessToken:token})
                //跳转home页
                this.$router.push({
                  name:'functionItem'
                });
              }else{
                //todo 跳转错误页面
                //关闭加载中
                this.loading=false
                //提示框
                this.$MessageBox.alert(msg, 'Message', {
                  confirmButtonText: 'OK'
                })
              }
          }else{
            //关闭加载中
            this.loading=false
            //提示框
            this.$MessageBox.alert('The system is busy. Please try again later', 'Message', {
              confirmButtonText: 'OK'
            })
          }
        })
      },
    }
}
</script>

<style>

</style>