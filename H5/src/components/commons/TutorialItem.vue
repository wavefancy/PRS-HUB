<template>
  <div>
    <Header></Header>
    <div class="py-md-32 position-relative" style="padding-top: 0rem!important;">
        <div class="container-lg max-w-screen-xl tuorial">
            <h2>Tutorial</h2>
            <div class="row align-items-center content ">
                <h3>Overview</h3>
                <p class="navbar-text">This tutorial guides you how to use our PRS-hub platform and provides a step-by-step guide to perform 10 high performance polygenic risk score (PRS) methods deployed on our PRS-hub platform at one time and gets SNPs estimates for generating PRS scores. The aim of this tutorial is to provide a simple introduction and usage of PRS analyses to those new to PRS, while equipping existing users with a better and simplified of the processes and implementation of popular PRS methods. </p>
            </div>
            <div class="row align-items-center content ">
                <h3>GWAS Summary Statistics</h3>
                <p class="navbar-text">On this page, you first need to fill in the name and description of your GWAS Summary data, then upload your GWAS Summary Statistics file. The summary statistics file must have the following format (including the header line):</p>
                <p>
                    <table class="table">
                        <th>CHR</th>
                        <th>SNP</th>
                        <th>POSITION</th>
                        <th>A1</th>
                        <th>A2</th>
                        <th>A1_FREQ</th>
                        <th>BETA</th>
                        <th>SE</th>
                        <th>P</th>
                        <th>N-MISSING</th>
                        <th> N-OBSERVATION</th>
                        <tr>
                            <td>1</td>
                            <td>rs3131962</td>
                            <td>756604</td>
                            <td>A</td>
                            <td>G</td>
                            <td>0.3417</td>
                            <td>-0.00211532</td>
                            <td>0.00301666</td>
                            <td>0.483171</td>
                            <td>97704</td>
                            <td>388028</td>
                        </tr>
                    </table>
                </p>
                <p class="navbar-text">where the CHR is the the specific chromosome, SNP is the rs ID, POSITION is the SNP position ID, A1 is the effect allele, A2 is the non-effect allele, A1_FREQ is the effect allele frequency, BETA is the effect of the A1 allele, SE is standard error, P is the p-value of the effect, N-missing is the missing sample size, N-observation is the effect sample size.</p>
                <p class="navbar-text">The GWAS Summary Statistics you uploaded will display in My GWAS Summary Statistics Panel. The data you upload will only be stored on our platform for 30 days by default, but you can extend it for another 30 days by pressing the refresh button.</p>
            </div>
            <div class="row align-items-center content ">
                <h3>LD References</h3>
                <p class="navbar-text">This page is not necessarily required for you to fill in, since we have already prepared the LD references for five ancestries (European, EUR; Admixed American, AMR; East Asian, EAS; South Asian, SAS; African, AFR) both from 1000 Genome Project and UK Biobank corresponding with the 10 PRS methods deployed on our website. However, if you want to run the PRS analyses using your own LD references, you first need to fill in the name and description of the LD References files you will upload next, then upload your LD References in pgen files (file_name.pgen; file_name.psam; file_name.pvar), compress the files in file_name.tar.gz format (please ensure the same file_name before and after compression). Now we only support you uploading your original LD References in pgen files after decompression, and we will convert the pgen format files you uploaded into the LD references files suitable for running each PRS method.</p>
                <p class="navbar-text">After you finish uploading the LD reference file, it will be displayed in My LD References panel. As the same, the data you upload will only be stored on our platform for 30 days by default, but you can extend it for another 30 days by pressing the refresh button. </p>
            </div>
            <div class="row align-items-center content ">
                <h3>Single Ancestry</h3>
                <p class="navbar-text">On this page, we provide you simple 3-steps to create paralleling jobs of SNPs effect size estimating for polygenic risk score analyses from 9 PRS methods at one time. The 9 PRS methods displayed here are: P+T, LDpred2, lassosum, PRS-CS, PRS-CSx, SbayesR, DBSLMM, SDPR, SBLUP, JAMPred. All of them create PRS analysis for single ancestry at a time.</p>
                <p class="navbar-text">Step 1: Select GWAS Summary Statistics</p>
                <p class="navbar-text">Here you just need to select the GWAS Summary Data you uploaded to implement PRS analyses. The summary statistics file you selected must follow our demanding format (see on GWAS Summary Statistics page).</p>
                <p class="navbar-text">Step 2: Select LD References</p>
                <p class="navbar-text">Here we recommend you select the LD references we prepared for five ancestries (European, EUR; Admixed American, AMR; East Asian, EAS; South Asian, SAS; African, AFR) both from 1000 Genome Project and UK Biobank, which we have tuned well to match each PRS method deployed on our website. If you want to analyse other ancestries or just want to implement your own LD references data, you can upload the LD references data on LD References page and select it here, We will help you to transform the LD reference format in order to match the PRS methods you select to analyse.</p>
                <p class="navbar-text">Step 3: Select Algorithms</p>
                <p class="navbar-text">Here displays 10 PRS methods, you can select 1-10 of them to create PRS analysis for SNPs effect size estimation. For each PRS method you have selected, you can press the “setting” button to configure the parameters, we have provided the default values or ranges of parameters in the input box to help you to fill in the right parameters and quickly create the analysis jobs. After finishing selecting your intended PRS methods and configuration of parameters for specific methods, please press the “submit” button on top right-hand corner of the page, write a name for your PRS jobs and submit after checking the parameters. Then the page will jump to the Jobs page.</p>
            </div>
            <div class="row align-items-center content ">
                <h3>Multiple Ancestries</h3>
                <p class="navbar-text">This page deployed the PRS-CSx method, which can create PRS analysis jobs from multiple-ancestries at a time. Similar to the Single Ancestry Page, you can quickly submit your PRS jobs in simple 3-steps.</p>
                <p class="navbar-text">Step 1: Select GWAS Summary Statistics</p>
                <p class="navbar-text">First you need to select the GWAS Summary Data you uploaded to implement subsequent PRS analyses. As this page deployed PRS method of polygenic prediction from multiple populations, you need to upload GWAS summary statistics derived from at least two different ancestries. The summary statistics file you selected must follow our demanding format (see on GWAS Summary Statistics page).</p>
                <p class="navbar-text">Step 2: Select LD References</p>
                <p class="navbar-text">Here we recommend you select the LD references we prepared for five ancestries (European, EUR; Admixed American, AMR; East Asian, EAS; South Asian, SAS; African, AFR) both from 1000 Genome Project and UK Biobank, which we have tuned well to match each PRS method deployed on our website. However, if you want to analyse other ancestries or just want to implement your own LD references data, you can upload the LD references data on LD References page and select it here. Different with this option in Single Ancestry Page, here you need to select at least two LD references from different ancestries to make different combinations of GWAS Summary Statistics and LD references to implement PRS-CSx analysis across populations. The combinations of GWAS Summary Statistics and LD references you selected will be displayed below this step.</p>
                <p class="navbar-text">Step 3: Select Algorithm</p>
                <p class="navbar-text">Currently we only display PRS-CSx method here, if you want to try PRS analysis from multiple ancestries at a time, you can select this method and press the “setting” button to optimize the parameters configration before submit your PRS jobs. After your submission, the page will defaultly transmit to the Jobs page to check your jobs status and download the results.</p>
            </div>
            <div class="row align-items-center content ">
                <h3>Jobs</h3>
                <p class="navbar-text">On this page, you will see all the PRS jobs you submitted and check or monitor the realtime status of your PRS jobs here. You can press every single job to pop a window filled in the PRS method, name of data and parameters you selected for building this job, which helps you to check the jobs details. For job status, we have 5 statuses for your PRS jobs: Submitted, Risk, Started, Failed and Finished. After your submission, all of the PRS jobs are in “Submitted” status, then our platform will automatically check whether the GWAS summary data and LD references you uploaded have the proper formats and necessary contents to continue to the next step. If they meet with our requirments, the Jobs status will continue to “Started” status, otherwise it will turn to the “Risk" status, and you can press “Error Report” button to see why the Jobs is at risk, so you can re-upload datasets and resubmit the PRS Jobs again. If the Jobs status displays “Failed”, it means there’s something wrong with your parameters setting or some other problems, we will also provide you the “Error Report” derived from the standard error report of cromwell workflow wdl behind our platform to help you to find the problems and revise the configuration for your PRS jobs. Finally, if the jobs status displays “Finished”, congratulations, you have successfully completed you PRS analysis. Next you can press “Download Results” to download your results and got your results for next PRS score construction (plink --score).</p>
            </div>
        </div>
    </div>
  </div>
</template>

<script>
import Header from "@/components/commons/HeaderItem.vue"
export default {
    name:"TutorialItem",
    components: {
        Header
    }

}
</script>

<style>
.tuorial{
    padding: 5rem;
}
.content{
    padding: 1rem 2rem;
}
.table{
    border-top: 2px solid #fff !important;
}
.table>:not(:first-child) {
    border-top: 2px solid #fff !important;
}
</style>