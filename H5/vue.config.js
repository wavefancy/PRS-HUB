const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: false,
  lintOnSave:false ,// 关闭语法检查
  //配置代理跨域
  devServer: {
    proxy: {
      "/api": {
        target: "http://118.195.223.193:9090/prs/hub/",
        // target: "http://localhost:9090/prs/hub/",
        pathRewrite: { '^/api': '' },
      },
      "/prd-api": {
        target: "http://118.195.223.193:9090/prs/hub/",
        pathRewrite: { '^/prd-api': '' },
      },
    },
  },
})
