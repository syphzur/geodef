const IN_PRODUCTION = process.env.NODE_ENV === 'production';

module.exports = {
  plugins: [
    IN_PRODUCTION &&
      require('@fullhuman/postcss-purgecss')({
        content: [
          './public/**/*.html',
          './src/**/*.vue',
          'node_modules/vuetify/src/**/*.ts'
        ],
        defaultExtractor(content) {
          const contentWithoutStyleBlocks = content.replace(
            /<style[^]+?<\/style>/gi,
            ''
          );
          return (
            contentWithoutStyleBlocks.match(
              /[A-Za-z0-9-_/:]*[A-Za-z0-9-_/]+/g
            ) || []
          );
        },
        whitelist: [
          'v-application',
          'v-application--wrap'
        ],
        safelist: [
          /-(leave|enter|appear)(|-(to|from|active))$/,
          /^(?!(|.*?:)cursor-move).+-move$/,
          /^router-link(|-exact)-active$/,
          /data-v-.*/,
          /col-*/,
          /^v-((?!application).)*$/,
          /^\.theme--light*/,
          /.*-transition/
        ]
      })
  ]
};
