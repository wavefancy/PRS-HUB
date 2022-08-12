<template>
  <div style="width: 100%;">

    <uploader :options="options" :autoStart="false"  :fileStatusText="fileStatusText"
        @file-success="onFileSuccess"  @file-added="fileAdded"  @file-error="onFileError"
        class="uploader-example">
        <uploader-unsupport></uploader-unsupport>
        <uploader-drop>
            <uploader-btn :attrs="attrs" single>上传</uploader-btn>
        </uploader-drop>
        <uploader-list></uploader-list>
    </uploader>
</div>

</template>

<script>
export default {
    name:"SimpleUploaderItem",
    data () {
        return {
            options: {
                //目标上传 URL
                target: process.env.VUE_APP_BASE_PRS_EPORTAL+'/bigfile/uploadPost', //上传地址
                //分块时按照该值来分 10 * 1024 * 1024 : 10mb
                chunkSize: 10*1024 * 1024,
                //是否测试每个块是否在服务端已经上传了，主要用来实现秒传、跨浏览器上传等，默认 true
                testChunks: false,
                headers: { //设置header
                    accessToken: localStorage.getItem("accessToken")
                },
                //单文件上传
                singleFile: true,
                query: { //传参,没有可以不传
                    // module: 10
                }
            },
            attrs: {
                accept: '*' //接受文件类型
            },
            //文件上传状态
            fileStatusText:{
                success: 'success',
                error: 'error',
                uploading: 'uploading',
                paused: 'paused',
                waiting: 'waiting'
            }

        }
    },
    methods: {
        //大文件上传标签删除
        handleClose() {
            console.log("handleClose")
        },
        //大文件上传所需
        fileAdded(file) {
            //选择文件后暂停文件上传，上传时手动启动
            file.pause()
            
        },
        onFileError(file) {
            console.log('error', file)
        },
        onFileSuccess(rootFile, file, response, chunk) {
            console.log("上传成功")
            console.log("rootFile="+rootFile)
            console.log("file="+file)
            console.log("response="+response)
            console.log("chunk="+chunk)
        },

    }

}
</script>

<style scoped>
	h1,
	h2 {
		font-weight: normal;
	}

	ul {
		list-style-type: none;
		padding: 0;
	}

	li {
		display: inline-block;
	}

	.uploader-list>ul>li {
		width: 100%;
		color: red;
		margin-bottom: 0;
	}

	a {
		color: #42b983;
	}

	.fileupload {
		width: 50%;
		margin-left: 25%;
	}
</style>
