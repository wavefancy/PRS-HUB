<template>
    <div  class="row">
        <h4 class="mb-4">Step 2 Select LD reference panel:</h4>
        <div class="col-xl-3 col-sm-4">
            <select class="form-select" v-model="ldFileId" @change="referenceSelect">
                <option :value="-1">please select</option>
                <option v-for="reference in referenceList" :key="reference.id"   :value="reference.id">{{reference.name}}</option>
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
            ldFileId:-1,
            referenceList:[
                // {
                //     name:'1000G EUR'
                // },
                // {
                //     name:'1000G AFR'
                // },
                // {
                //     name:'1000G SAS'
                // },
                // {
                //     name:'1000G EAS'
                // }
            ]
        }
    },
    methods: {
        referenceSelect(){
            let selectId = this.ldFileId
             var ldName = ""
            for(let i=0 ;i<this.referenceList.length;i++){
                let ldFile = this.referenceList[i]
                if(selectId === ldFile.id){
                    ldName = ldFile.name
                    break
                }
            }
            this.$bus.$emit("referenceSelect",selectId, ldName)
        }
    },
    mounted(){
        let subData = {
          fileType:'LD',
          parsingStatus:'Y'
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