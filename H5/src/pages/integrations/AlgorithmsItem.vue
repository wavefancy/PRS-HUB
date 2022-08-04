<template>
  <div>
        <h4 class="mb-4">
            Step 3 Algorithms choice
        </h4>
        <div class="row row-cols g-4 g-xl-6" style="">
            <div class="col-xl-3 col-sm-6" v-for="algorithm in algorithmsShow" :key="algorithm.id">
            <div class="card shadow-4-hover">
                <div class="card-body pb-5">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                    <h6 class="mb-2">{{algorithm.name}}</h6>
                    </div>
                    <div class="text-end">
                    <div class="form-check form-switch me-n2">
                        <input class="form-check-input" type="checkbox" role="switch" 
                        @change="algorithmSelected($event,algorithm.id)"
                        :id= "algorithm.name" :ref="algorithm.name"  v-model="algorithm.checkVal">
                    </div>
                    </div>
                </div>
                <hr class="my-5" />
                <div class="text-end" >
                    <p class="discription text-heading text-primary-hover text-sm font-semibold"
                    data-bs-toggle="modal" 
                    data-bs-target="#discription" 
                    @click="changeShowDiscription(algorithm.summary)"> 
                        <i class="bi bi-chat-square-text"></i>
                         Description </p>
                    <p class="setting lh-none text-heading text-primary-hover text-sm font-semibold" 
                    :data-bs-toggle="algorithm.toggleName" 
                    @click="clickSettings(algorithm.id)"
                    data-bs-target="#modalInstallApp" >
                    <i class="bi bi-gear-fill me-2 text-muted"></i>Settings
                    </p>
                </div>
                </div>
            </div>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    name:"AlgorithmsItem",
    props:['algorithmsShow'],
    methods: {
        algorithmSelected(e,id){
           //触发总线绑定的algorithmChecked函数
           this.$bus.$emit('algorithmChecked',e,id)
        },
        clickSettings(id){
            this.$bus.$emit('changeShowParameters',id)
        },
        changeShowDiscription(val){
            this.$bus.$emit('changeShowDiscription',val)
        }
    }
}
</script>

<style>
.setting {
    width: 50%;
    float: right;
}
.discription{
    text-align: left;
    width: 50%;
    float: left;
}
</style>