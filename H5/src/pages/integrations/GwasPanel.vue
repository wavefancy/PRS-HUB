<template>
    <div  class="row">
        <div class="col-xl-3 col-sm-4">
            <h4 class="mb-4">Step 1 Select GWAS Summary Statistics</h4>
            <select class="form-select" v-model="gwasFileId" @change="gwasPanelSelect">
                <option value="0">please select</option>
                <option v-for="gwasFile in gwasFileList" :key="gwasFile.id" :value="gwasFile.id">{{gwasFile.fileName}}</option>
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
            gwasFileId:0,
            gwasFileList:[
            ]
        }
    },
    methods: {
        gwasPanelSelect(){
            this.$bus.$emit("gwasPanelSelect", this.gwasFileId)
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
              this.gwasFileList =resData.resDTOList
            }
          }
        })
    }
}
</script>

<style>

</style>