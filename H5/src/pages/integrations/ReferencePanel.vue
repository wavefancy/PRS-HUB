<template>
    <div  class="row">
        <h4 class="mb-4">Step 2 Select LD reference panel:</h4>
        <div class="col-xl-3 col-sm-4">
            <select class="form-select" v-model="referencePanel" @change="referenceSelect">
                <option value="">please select</option>
                <option v-for="reference in referenceList" :key="reference.id" :value="reference.id">{{reference.name}}</option>
            </select>
        </div>
    </div>
</template>

<script>
import {Prs} from "@/api"
export default {
    name:"ReferencePanel",
    data () {
        return {
            referencePanel:"",
            referenceList:[
                {
                    name:'1000G EUR'
                },
                {
                    name:'1000G AFR'
                },
                {
                    name:'1000G SAS'
                },
                {
                    name:'1000G EAS'
                }
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
          fileType:'LD',
        }
        //提交参数
        Prs.getFileList(subData).then(response => {
          if(response.code===0){
            const resData = response.data;
            if(resData.code===0){
              const fileList =resData.resDTOList
              fileList.forEach(file => {
                  let nameData = {
                      name:file.fileName,
                      id:file.id
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