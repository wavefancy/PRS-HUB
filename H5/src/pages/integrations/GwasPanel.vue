<template>
    <div  class="row">
        <h4 class="mb-4">Step 1 Select GWAS Summary Statistics</h4>
        <div class="col-xl-3 col-sm-4">
            <select class="form-select" v-model="gwasFileId" @change="gwasPanelSelect">
                <option value="0" ref="0">please select</option>
                <option v-for="gwasFile in gwasFileList" :key="gwasFile.id" :value="gwasFile.id" >{{gwasFile.fileName}}</option>
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
            var nameVal = ""
            for(let i=0 ;i<this.gwasFileList.length;i++){
                let gwasFile = this.gwasFileList[i]
                if(this.gwasFileId === gwasFile.id){
                    nameVal = gwasFile.fileName
                    break
                }
            }
            this.$bus.$emit("gwasPanelSelect", this.gwasFileId,nameVal)
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