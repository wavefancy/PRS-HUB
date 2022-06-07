<template>
<div>
    <HeaderItem></HeaderItem>
    <div class="px-5 py-5 p-lg-0 h-screen bg-surface-secondary d-flex flex-column justify-content-center">
        <div class="d-flex justify-content-center">
            <div class="col-12 col-md-9 col-lg-6 min-h-lg-screen d-flex flex-column justify-content-center px-lg-20 position-relative">
                <div class="row">
                    <div class="col-lg-10 col-md-9 col-xl-7 mx-auto">
                        <div class="svg-div" v-if="flag">
                            <svg  t="1653448837759" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="6490" width="200" height="200"><path d="M512 8C790.352 8 1016 233.648 1016 512S790.352 1016 512 1016 8 790.352 8 512 233.648 8 512 8z m0 48C260.16 56 56 260.16 56 512S260.16 968 512 968 968 763.84 968 512 763.84 56 512 56z m214.624 327.04a24 24 0 0 1 1.648 32.112l-1.648 1.824-236.448 236.448a24 24 0 0 1-32.112 1.648l-1.824-1.648-137.216-137.216a24 24 0 0 1 32.128-35.584l1.824 1.648 120.224 120.224 219.488-219.472a24 24 0 0 1 33.936 0z" fill="#3BC48B" p-id="6491" data-spm-anchor-id="a313x.7781069.0.i0" class="selected"></path></svg>
                        </div>
                        <div class="svg-div" v-if="flag">
                            <p style="margin: auto; color: #f56c6c;">Registration is successful.</p>
                        </div>
                        <div class="svg-div" v-if="flag">
                            <div style="margin: auto;">
                                <router-link to="/login" class="btn btn-lg btn-primary shadow-sm mx-2 px-lg-8">
                                    Sign in
                                </router-link>
                            </div>
                        </div>
                        <div v-if="!flag">
                            <svg t="1653448962076" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="6641" width="200" height="200"><path d="M512 8C790.352 8 1016 233.648 1016 512S790.352 1016 512 1016 8 790.352 8 512 233.648 8 512 8z m0 48C260.16 56 56 260.16 56 512S260.16 968 512 968 968 763.84 968 512 763.84 56 512 56z m148.336 284.64a24 24 0 0 1 32.128 35.6l-141.152 141.136 141.152 141.136a24 24 0 0 1-32.128 35.6l-1.824-1.648-141.136-141.152-141.12 141.152-1.84 1.648a24 24 0 0 1-32.112-35.6l141.12-141.136-141.12-141.12A24 24 0 0 1 374.4 340.64l1.824 1.648 141.136 141.12 141.136-141.12z" fill="#FA3F3F" p-id="6642"></path></svg>
                        </div>
                        <div class="svg-div" v-if="!flag">
                            <p style="margin: auto; color: #f56c6c;">Registration is failed.</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</template>

<script>
import HeaderItem from "@/components/commons/HeaderItem.vue"

export default {
    name:"ResultItem",
    components: {
        HeaderItem
    },
    data () {
        return {
            code:0
        }
    },
    computed: {
        flag(){
            return this.code===0 ? true : false
        }
    }
}
</script>

<style>
    .svg-div{
        display: flex;
        margin-bottom: 1rem;
    }
    .icon{
        margin: auto;
        width: 6rem;
        height: 6rem;
    }
</style>