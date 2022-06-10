const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  lintOnSave:false ,// 关闭语法检查
  //配置代理跨域
  devServer: {
    proxy: {
      "/api": {
        target: "http://11.11.11.55:9091/prs/hub/",
        pathRewrite: { '^/api': '' },
      },
      "/prd-api": {
        target: "http://192.168.118.81:9091/prs/hub/",
        pathRewrite: { '^/prd-api': '' },
      },
    },
  },
})
