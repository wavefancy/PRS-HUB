<template>
       <nav
        class="navbar show navbar-vertical h-lg-screen navbar-expand-lg px-0 py-3 navbar-light bg-white border-bottom border-bottom-lg-0 border-end-lg"
        id="sidebar">
        <div class="container-fluid">
          <!-- Toggler -->
          <button class="navbar-toggler ms-n2" type="button" data-bs-toggle="collapse" data-bs-target="#sidebarCollapse"
            aria-controls="sidebarCollapse" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
          </button>
          <!-- Brand -->
          <a class="logo navbar-brand py-lg-2 mb-lg-5 px-lg-6 me-0 row " href="/">
            <div  class="" style="padding: 0px;width: auto;" ><img style="height: 2.875rem;padding: 0px;" :src="imgUrls.primary" alt="..."></div>
            <div class=" prs" > <p>PRS-hub</p> </div>
          </a>
          <!-- 用户菜单(mobile) -->
          <div class="navbar-user d-lg-none">
            <!-- Dropdown -->
            <div class="dropdown">
              <!-- Toggle -->
              <a href="#" id="sidebarAvatar" role="button" data-bs-toggle="dropdown" aria-haspopup="true"
                aria-expanded="false">
                <div class="avatar-parent-child" :class="isLogin" style="border-radius: 50%;" >
                    <svg  t="1646979744016" class="login-icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="11215" width="200" height="200"><path d="M512 0A512 512 0 1 0 1024 512 512 512 0 0 0 512 0z m0 34.133333a477.476571 477.476571 0 0 1 359.862857 791.747048c-1.852952-5.36380999-3.657143-10.727619-5.753905-15.993905a377.465905 377.465905 0 0 0-198.70476199-204.312381 4.583619 4.583619 0 0 1-0.731428-0.292571 213.333333 213.333333 0 1 0-309.345524 0l-1.31657101 0.536381a376.88076201 376.88076201 0 0 0-198.119619 204.01980899 312.417524 312.417524 0 0 0-5.753905 15.99390501A477.476571 477.476571 0 0 1 512 34.133333z m119.466667 557.68990501a179.2 179.2 0 0 1-298.666667-133.12 179.2 179.2 0 1 1 298.666667 133.12z m-452.705524 262.241524c1.26781001-4.437333 2.438095-8.923429 3.803428-13.16571401A344.746667 344.746667 0 0 1 385.219048 629.955048a212.163048 212.163048 0 0 0 253.561904 0 343.82019001 343.82019001 0 0 1 202.70323799 210.944c1.365333 4.193524 2.535619 8.728381 3.75466701 13.16571399a476.696381 476.696381 0 0 1-666.428952 0z m0 0" p-id="11216" fill="#ffffff"></path></svg>
                  <span class="avatar-child avatar-badge bg-success"></span>
                </div>
              </a>
              
              <!-- Menu -->
              <div class="dropdown-menu dropdown-menu-end" aria-labelledby="sidebarAvatar">
              
                <a class="dropdown-item" v-for="(menu, index) in userMenus" :key="index" :href="menu.hrefUrl">
                  {{menu.val}}
                </a>
                <hr class="dropdown-divider">
                <a href="#" class="dropdown-item">Logout</a>
              </div>
            </div>
          </div>
          <!-- Collapse -->
          <div class="collapse navbar-collapse" id="sidebarCollapse">

            
            <!-- Navigation -->
            <ul class="navbar-nav">
              <li class="nav-item" v-for="(navigation,index) in navigations" :key="index">
                <a class="nav-link" :href="`#${navigation.name}`" data-bs-toggle="collapse" 
                role="button" aria-expanded="true" aria-controls="sidebar-integrations">
                  <i class="bi" :class="navigation.sign"></i> {{navigation.name}}
                </a>
                <div class="collapse" active-class="show" :id="navigation.name">
                  <ul class="nav nav-sm flex-column">
                    <li class="nav-item" v-for="(inner,index) in navigation.inners" :key="index">
                      <router-link :to="inner.path" class="nav-link" active-class="font-bold">
                        {{inner.name}}
                      </router-link>
                    </li>
                  </ul>
                </div>
              </li>

            </ul>
            <!-- Divider -->
            <hr class="navbar-divider my-4 opacity-70">
            <!-- Documentation -->
            <ul class="navbar-nav">
              <li>
                <span class="nav-link text-xs font-semibold text-uppercase text-muted ls-wide">
                  Resources
                </span>
              </li>
              <li class="nav-item">
                <a class="nav-link py-2" href="/docs">
                  <i class="bi bi-code-square"></i> Documentation
                </a>
              </li>
            </ul>
            <!-- Push content down -->
            <div class="mt-auto"></div>
            <!-- User menu -->
            <div class="my-4 px-lg-6 position-relative">
              <div class="dropup w-full">
                <button
                  class="d-flex w-full py-3 ps-3 pe-4 align-items-center shadow shadow-3-hover rounded-3"
                  :class="isLogin"
                  type="button" :data-bs-toggle="dropdown" aria-expanded="false">
                  <span class="me-3">
                    <svg t="1646979744016" class="login-icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="11215" width="200" height="200"><path d="M512 0A512 512 0 1 0 1024 512 512 512 0 0 0 512 0z m0 34.133333a477.476571 477.476571 0 0 1 359.862857 791.747048c-1.852952-5.36380999-3.657143-10.727619-5.753905-15.993905a377.465905 377.465905 0 0 0-198.70476199-204.312381 4.583619 4.583619 0 0 1-0.731428-0.292571 213.333333 213.333333 0 1 0-309.345524 0l-1.31657101 0.536381a376.88076201 376.88076201 0 0 0-198.119619 204.01980899 312.417524 312.417524 0 0 0-5.753905 15.99390501A477.476571 477.476571 0 0 1 512 34.133333z m119.466667 557.68990501a179.2 179.2 0 0 1-298.666667-133.12 179.2 179.2 0 1 1 298.666667 133.12z m-452.705524 262.241524c1.26781001-4.437333 2.438095-8.923429 3.803428-13.16571401A344.746667 344.746667 0 0 1 385.219048 629.955048a212.163048 212.163048 0 0 0 253.561904 0 343.82019001 343.82019001 0 0 1 202.70323799 210.944c1.365333 4.193524 2.535619 8.728381 3.75466701 13.16571399a476.696381 476.696381 0 0 1-666.428952 0z m0 0" p-id="11216" fill="#ffffff"></path></svg>
                  </span>
                  <span class="flex-fill text-start text-sm font-semibold">
                    {{name}}
                  </span>
                  <span>
                    <i class="bi bi-chevron-expand text-white text-opacity-70"></i>
                  </span>
                </button>
                <div class="dropdown-menu dropdown-menu-end w-full">
                  <div class="dropdown-header">
                    <span class="d-block text-sm text-muted mb-1">Signed in as</span>
                    <span class="d-block text-heading font-semibold">{{name}}</span>
                  </div>
                  <div class="dropdown-divider"></div>
                  <div>
                    <a class="dropdown-item" v-for="(menu, index) in userMenus" :key="index" :href="menu.hrefUrl">
                      <i class="bi me-3" :class="menu.addClass"></i>{{menu.val}}
                    </a>
                  </div>
                  <div class="dropdown-divider"></div>
                  <a class="dropdown-item" href="#">
                    <i class="bi bi-box-arrow-left me-3"></i>Logout
                  </a>
                </div>
              </div>
            </div>
          </div>
        </div>
      </nav>
</template>

<script>
import {Account} from "@/api"
import { isEmpty }  from "@/utils/validate"

export default {
    name:"SidebarItem",
    data() {
      return {
        name:"User name",
        isLogin:"login",
        dropdown:"dropdown",
        userMenus:[
          // {
          //   val:"Home",
          //   hrefUrl:"#",
          //   addClass:"bi-house"
          // },
          // {
          //   val:"Profile",
          //   hrefUrl:"#",
          //   addClass:"bi-pencil"
          // },
          {
            val:"Settings",
            hrefUrl:"#",
            addClass:"bi-gear"
          }
        ],
        imgUrls:{
          primary:"./img/logos/PRS-hub.png"
        },
        navigations:[
          {
            name:"Setup",
            sign:"bi-terminal",
            inners:[
              {
                name:"Single Ancestry",
                path:"/analysis"
              },
              {
                name:"GWAS Summary Statistics",
                path:"/gwasreference"
              },
              {
                name:"LD reference",
                path:"/ldreference"
              }
            
            ]
          },
          {
            name:"Jobs",
            sign:"bi-kanban",
            inners:[
              {
                name:"Statistics",
                path:"/statistics"
              }
            
            ]
          }
        ]
        
      }
    },
    mounted () {
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
                this.name=user.firstName+" "+user.lastName
              }
          }        
        })
    }
}
</script>
<style scoped>
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
  .login-icon{
    width: auto;
    height: 2rem;
  }
  .unLogin{
    background-color: #8a8a8a;
    color: #ffffff;
  }
  .login{
    background-color: #796CFF;
    color: #ffffff;
  }
</style>