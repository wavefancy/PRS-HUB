<template>
    <nav class="navbar navbar-expand-lg navbar-light px-0 py-3">
        <div class="container-xl max-w-screen-xl">
            <!-- Logo -->
            <a class="logo navbar-brand py-lg-2 px-lg-6 me-0 row " href="/">
            <div  class="" style="padding: 0px;width: auto;" ><img style="height: 2.875rem;padding: 0px;" :src="imgUrls.primary" alt="..."></div>
            <div class=" prs" > <p>PRS-hub</p> </div>
            </a>
            <!-- Navbar toggle -->
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarCollapse" aria-controls="navbarCollapse" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            <!-- Collapse -->
            <div class="collapse navbar-collapse" id="navbarCollapse">
                <!-- Nav -->
                <ul class="navbar-nav mx-lg-auto">
                    <li class="nav-item">
                        <a class="nav-link" href="#">Home</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#">About</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#">Help</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#">Contact</a>
                    </li>
                </ul>
                <!-- Right navigation -->
                <div class="navbar-nav ms-lg-4">
                    <router-link class="nav-item nav-link" to="/login">Sign in</router-link>
                </div>
                <!-- Action -->
                <div class="d-flex align-items-lg-center mt-3 mt-lg-0">
                    <router-link  to="/register" class="btn btn-sm btn-neutral w-full w-lg-auto">
                        Register
                    </router-link>
                </div>
            </div>
        </div>
    </nav>
</template>

<script>
export default {
    name:"HeaderItem",
    data () {
        return {
            imgUrls:{
                primary:"./img/logos/PRS-hub.png"
            },
        }
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
</style>