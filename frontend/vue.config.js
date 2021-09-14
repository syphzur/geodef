const CompressionPlugin = require('compression-webpack-plugin');

module.exports = {
  configureWebpack: {
    plugins: [
      new CompressionPlugin({
        algorithm: 'gzip'
      })
    ],
    optimization: {
      splitChunks: {
        minSize: 10000,
        maxSize: 250000
      }
    }
  },
  css: {
    loaderOptions: {
      scss: {
        prependData: '@import "~@/assets/styles.scss";'
      }
    }
  }
};
