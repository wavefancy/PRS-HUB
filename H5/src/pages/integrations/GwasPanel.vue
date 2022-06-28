<template>
    <div  class="row">
        <div class="col-xl-3 col-sm-4">
            <h4 class="mb-4">Step 1 Select GWAS Summary Statistics</h4>
            <select class="form-select" v-model="referencePanel" @change="referenceSelect">
                <option value="">please select</option>
                <option v-for="reference in referenceList" :key="reference.id" :value="reference.name">{{reference.name}}</option>
            </select>
        </div>
    </div>
</template>

<script>
import {Prs} from "@/api"
export default {
    name:"GwasPanel",
    data () {
        return {
            referencePanel:"",
            referenceList:[
            ]
        }
    },
    methods: {
        referenceSelect(){
            this.$bus.$emit("referenceSelect", this.referencePanel)
        }
    },
    mounted(){
        let subData = {
          fileType:'GWAS',
        }
        //提交参数
        Prs.getFileList(subData).then(response => {
          if(response.code===0){
            const resData = response.data;
            if(resData.code===0){
              const fileList =resData.resDTOList
              fileList.forEach(file => {
                  let nameData = {
                      name:file.fileName
                  }
                  this.referenceList.push(nameData)
              });
            }
          }
        })
    }
}
</script>

<style>

</style>