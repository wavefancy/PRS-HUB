<template>
<div class="outer">
  <Header></Header>
  <vue-element-loading :active="loading" :is-full-screen="true" spinner="ring"/>
  <div class="px-5 py-20  bg-surface-secondary d-flex flex-column justify-content-center">
    <div class="d-flex justify-content-center">
      <div class="col-12 col-md-9 col-lg-8 min-h-lg-screen d-flex flex-column justify-content-center py-lg-16 px-lg-20 position-relative">
        <div class="row">
          <div class="col-lg-10 col-md-9 col-xl-7 mx-auto">
            <div class="text-center mb-12">
              <!-- <a class="navbar-brand py-lg-2 mb-lg-5 px-lg-6 me-0" href="/" style="color:#796CFF">
                <img style="height: 2.875rem;"  :src="imgUrls.primary"  alt="...">PRS-hub
              </a> -->
              <h1 class="ls-tight font-bolder mt-6">
                Create your account
              </h1>
              <p class="mt-2">It's free and easy</p>
            </div>
            <div class="mb-2 row">
              <div class="inlinediv col-lg-6 col-md-12">
                <div class="mr">
                  <label class="form-label">First name<img class="asterisk" :src="imgUrls.asterisk" > </label>
                  <input type="text" class="form-control" ref="firstName" @blur='checkName'  v-model.trim="firstName" placeholder="Your First name">
                </div>
              </div>
              <div class="inlinediv col-lg-6 col-md-12">
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
            <div  class="mb-2 row">
              <div class="inlinediv  col-lg-6 col-md-12">
                <div class="mr">
                  <label class="form-label">City<img class="asterisk" :src="imgUrls.asterisk" > </label>
                  <input type="text" class="form-control"  v-model.trim="city" placeholder="Your City">
                </div>
              </div>
              <div class="inlinediv  col-lg-6 col-md-12">
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
              <p style="font-size: xx-small;color: rgba(107,123,147);">Use 6 ~ 20 characters.</p>
            </div>
            <div class="mb-2">
              <label class="form-label" for="password">Confirm Password<img class="asterisk" :src="imgUrls.asterisk" > </label>
              <div class="outer">
                <input :type="passwordType" class="inner form-control" style="width: 90%;" ref="confirm" @blur="confirmPw" v-model.trim="confirm" placeholder="Confirm" autocomplete="current-password">
                <img class="eyes" :src="eyesImage" @click="clickEyes"/>
              </div>
              <p class="errrorMsg" v-if="errorCon">Different passwords</p>
            </div>
            <div class="mb-2">
              <div class="form-check">
                <input class="form-check-input" type="checkbox" v-model="agree" name="check_example" id="check-remind-me">
                <label class="form-check-label font-semibold text-muted" >
                  By creating an account means you agree to the <u class="my-a"  data-bs-toggle="modal" 
                    data-bs-target="#terms" >Terms and Conditions</u>, and our <u class="my-a" data-bs-toggle="modal" 
                    data-bs-target="#privacy">Privacy Policy</u>
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
  <div class="modal fade" id="terms"  ref="terms"
    tabindex="-1" aria-labelledby="terms" aria-hidden="true"   >
    <div class="modal-dialog modal-dialog-centered">
      <div class="modal-content">
        <i class="bi bi-x-lg off-x modal-off-x " data-bs-dismiss="modal" ></i>
        <div class="modal-body px-12" style="padding-top: 0.5rem;" >
            <h2 style="text-align: center; margin-bottom: 2rem;">{{terms.title}}</h2>
            <ol>
              <li class="navbar-text" v-for="(content,index) in terms.contents" :key="index" >{{content}}</li>
            </ol>
        </div>
      </div>
    </div>
  </div>
  <div class="modal fade" id="privacy"  ref="privacy"
    tabindex="-1" aria-labelledby="privacy" aria-hidden="true"   >
    <div class="modal-dialog modal-dialog-centered">
      <div class="modal-content">
        <i class="bi bi-x-lg off-x modal-off-x" data-bs-dismiss="modal" ></i>
        <div class="modal-body px-12" style="padding-top: 0.5rem;" >
            <h2 style="text-align: center; margin-bottom: 2rem;">{{privacy.title}}</h2>
            <ol>
              <li class="navbar-text" v-for="(content,index) in privacy.contents" :key="index" >{{content}}</li>
            </ol>
        </div>
      </div>
    </div>
  </div>
</div>
</template>

<script>
import Header from "@/components/commons/HeaderItem.vue"
import {Account} from "@/api"
import { checkEmail,checkPassword,isEmpty,checkName }  from "@/utils/validate"

export default {
    name:"RegisterItem",
    components: {
      Header
    },
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
        eyesType:false,
        //条款
        terms:{
          title:"Terms and conditions",
          contents:[
            "The materials and results contained in the PRS-hub website is provided for scientific research purposes only and, as such, should not be considered as a substitute for advice covering any specific situation.",
            "Although we make best efforts to ensure that the results are accurately computated by PRS methods of inclusion, the PRS-hub website accepts no responsibility for loss arising from reliance on  misinformed data (e.g. GWAS summary statistics, LD references, etc) uploaded from users. ",
            "The views and opinions of people who are not at the PRS-hub website and that appear on the website do not necessarily reflect the views of the PRS-hub website.",
            "The PRS-hub website does not endorse or recommend commercial products, processes or services and no such conclusion should be drawn from content provided in or linked from this website. ",
            "Links to other internet sites are provided only for the convenience of users of the PRS-hub website. The PRS-hub website is not responsible for the content of external internet sites.",
            "The PRS-hub website endeavours to maintain this site but does not warrant that the site will be continuously available or error-free, nor does the PRS-hub website warrant that this service will be free of defects or bugs or malicious software or code.",
            "Neither the PRS-hub website nor those contributing to the site shall be liable for any losses or damage that may result from use of the website as a consequence of any inaccuracies in, or any omissions from, the information, data, or results, which it may contain.",
            "We reserve the right to make changes to this website at any time without notice.",
            "You agree to use this site only for lawful purposes.",
            "If you do not accept these Terms and Conditions in full, you will cease using this site immediately."
            ]
        },
        //
        privacy:{
          title:"Privacy Policy",
          contents:[
            "Thank you for visiting the PRS-hub website and reviewing our privacy policy. The China National Genomics Data Center, of which PRS-hub website is a part, does not disclose, give, sell, or transfer any personal information about visitors to its websites, unless required by law or court order.",
            "The PRS-hub website is committed to protecting your privacy. We will not collect any personal information about you when you visit our website (other than the standard information automatically collected and stored in the Web server's logs) unless you choose to provide it to us.",
            'Detailed information regarding server logs, and voluntary submission of personal information, "cookies", security, and intrusion detection systems is provided below.'
          ]
        }
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
                this.$MessageBox.alert('Please click the activation link in the activation email to activate your account !', 'Message', {
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
                this.$MessageBox.alert(msg, 'Message', {
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
                this.$MessageBox.alert('The system is busy. Please try again later', 'Message', {
                  confirmButtonText: 'OK'
                })
              }
          }else{
            //todo 跳转错误页面
            //关闭加载中
            this.loading=false
            //提示框
            this.$MessageBox.alert('The system is busy. Please try again later', 'Message', {
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
      },
      showTC(){
        this.showDiscription=true
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
.my-a{
  color: blue;
}
.modal-off-x{
  text-align: right;
  margin-right: 0.5rem;
  margin-top: 0.5rem;
}
.modal-dialog {
    max-width: 40rem !important;
}
</style>