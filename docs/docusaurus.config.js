// @ts-check
// Note: type annotations allow type checking and IDEs autocompletion

const lightCodeTheme = require('prism-react-renderer/themes/github');
const darkCodeTheme = require('prism-react-renderer/themes/dracula');

/** @type {import('@docusaurus/types').Config} */
const config = {
  title: 'Android Testify',
  tagline: 'Add screenshots to your Android tests',
  onBrokenLinks: 'throw',
  onBrokenMarkdownLinks: 'warn',
  favicon: 'img/favicon.ico',

  // GitHub Pages
  baseUrl: '/android-testify/',
  url: 'https://ndtp.github.io/',
  organizationName: 'ndtp',
  projectName: 'android-testify',
  deploymentBranch: 'gh-pages',
  trailingSlash: false,

  // Even if you don't use internalization, you can use this field to set useful
  // metadata like html lang. For example, if your site is Chinese, you may want
  // to replace "en" with "zh-Hans".
  i18n: {
    defaultLocale: 'en',
    locales: ['en'],
  },

  presets: [
    [
      'classic',
      /** @type {import('@docusaurus/preset-classic').Options} */
      ({
        docs: {
          lastVersion: 'current',
          versions: {
            current: {
              label: '2.0.0-alpha01',
            },
          },
          sidebarPath: require.resolve('./sidebars.js'),
          // Please change this to your repo.
          // Remove this to remove the "edit this page" links.
          editUrl:
            'https://github.com/ndtp/android-testify/tree/issue-12/docusaurus/docs/',
        },
        blog: {
          showReadingTime: true,
          // Please change this to your repo.
          // Remove this to remove the "edit this page" links.
          editUrl:
            'https://github.com/ndtp/android-testify/tree/issue-12/docusaurus/docs/',
        },
        theme: {
          customCss: require.resolve('./src/css/custom.css'),
        },
        sitemap: {
          changefreq: 'weekly',
          priority: 0.5,
          ignorePatterns: ['/tags/**'],
          filename: 'sitemap.xml',
        },
      }),
    ],
  ],

  themeConfig:
    /** @type {import('@docusaurus/preset-classic').ThemeConfig} */
    ({
      navbar: {
        title: 'Android Testify',
        logo: {
          alt: 'Android Testify Logo',
          src: 'img/logo.svg',
        },
        items: [
          {to: '/docs/intro', label: 'Get Started', position: 'left'},
          {to: '/docs/category/recipes', label: 'Recipes', position: 'left'},
          {to: '/docs/category/extensions', label: 'Extensions', position: 'left'},
          {to: '/docs/migration', label: 'Migration', position: 'left'},
          {to: 'blog', label: 'Blog', position: 'left'},
          {
            type: 'docsVersionDropdown',
            position: 'right'
          },
          {
            href: 'https://github.com/ndtp/android-testify',
            label: 'GitHub',
            position: 'right',
          },
        ],
      },
      footer: {
        style: 'dark',
        links: [
          {
            title: 'Docs',
            items: [
              {
                label: 'Documentation',
                to: '/docs/intro',
              },
            ],
          },
          {
            title: 'Community',
            items: [
              {
                label: 'Stack Overflow',
                href: 'https://stackoverflow.com/questions/tagged/android-testify',
              },
            ],
          },
          {
            title: 'More',
            items: [
              // {
              //   label: 'Blog',
              //   to: '/blog',
              // },
              {
                label: 'GitHub',
                href: 'https://github.com/ndtp/android-testify',
              },
            ],
          },
        ],
        copyright: `Copyright © ${new Date().getFullYear()} ndtp. Built with Docusaurus.`,
      },
      prism: {
        theme: lightCodeTheme,
        darkTheme: darkCodeTheme,
        additionalLanguages: ['kotlin', 'groovy', 'bash', 'java'],
      },
    }),
};

module.exports = config;
