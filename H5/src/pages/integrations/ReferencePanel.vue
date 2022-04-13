<template>
    <div  class="row">
        <div class="col-xl-2 col-sm-4">
            <h4 class="mb-4">Step 2 Select LD reference panel:</h4>
            <select class="form-select" v-model="referencePanel" @change="referenceSelect">
                <option value="">please select</option>
                <option value="1000G EUR">1000G EUR</option>
                <option value="1000G AFR">1000G AFR</option>
                <option value="1000G SAS">1000G SAS</option>
                <option value="1000G EAS">1000G EAS</option>
            </select>
        </div>
    </div>
</template>

<script>
export default {
    name:"ReferencePanel",
    data () {
        return {
            referencePanel:""
        }
    },
    methods: {
        referenceSelect(){
            this.$bus.$emit("referenceSelect", this.referencePanel)
        }
    }
}
</script>

<style>

</style>