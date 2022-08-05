"use strict";(self.webpackChunkdocs=self.webpackChunkdocs||[]).push([[3199],{3905:function(e,t,n){n.d(t,{Zo:function(){return p},kt:function(){return m}});var r=n(7294);function o(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function s(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);t&&(r=r.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,r)}return n}function a(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?s(Object(n),!0).forEach((function(t){o(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):s(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function i(e,t){if(null==e)return{};var n,r,o=function(e,t){if(null==e)return{};var n,r,o={},s=Object.keys(e);for(r=0;r<s.length;r++)n=s[r],t.indexOf(n)>=0||(o[n]=e[n]);return o}(e,t);if(Object.getOwnPropertySymbols){var s=Object.getOwnPropertySymbols(e);for(r=0;r<s.length;r++)n=s[r],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(o[n]=e[n])}return o}var c=r.createContext({}),l=function(e){var t=r.useContext(c),n=t;return e&&(n="function"==typeof e?e(t):a(a({},t),e)),n},p=function(e){var t=l(e.components);return r.createElement(c.Provider,{value:t},e.children)},u={inlineCode:"code",wrapper:function(e){var t=e.children;return r.createElement(r.Fragment,{},t)}},f=r.forwardRef((function(e,t){var n=e.components,o=e.mdxType,s=e.originalType,c=e.parentName,p=i(e,["components","mdxType","originalType","parentName"]),f=l(n),m=o,d=f["".concat(c,".").concat(m)]||f[m]||u[m]||s;return n?r.createElement(d,a(a({ref:t},p),{},{components:n})):r.createElement(d,a({ref:t},p))}));function m(e,t){var n=arguments,o=t&&t.mdxType;if("string"==typeof e||o){var s=n.length,a=new Array(s);a[0]=f;var i={};for(var c in t)hasOwnProperty.call(t,c)&&(i[c]=t[c]);i.originalType=e,i.mdxType="string"==typeof e?e:o,a[1]=i;for(var l=2;l<s;l++)a[l]=n[l];return r.createElement.apply(null,a)}return r.createElement.apply(null,n)}f.displayName="MDXCreateElement"},3912:function(e,t,n){n.r(t),n.d(t,{assets:function(){return p},contentTitle:function(){return c},default:function(){return m},frontMatter:function(){return i},metadata:function(){return l},toc:function(){return u}});var r=n(7462),o=n(3366),s=(n(7294),n(3905)),a=["components"],i={},c="Write a test",l={unversionedId:"extensions/compose/test",id:"version-1.2.0-alpha01/extensions/compose/test",title:"Write a test",description:"In order to test a @Composable function, you must first declare an instance variable of the ComposableScreenshotRule class. Then, you can invoke the setCompose() method on the rule instance and declare any Compose UI functions you wish to test.",source:"@site/versioned_docs/version-1.2.0-alpha01/extensions/compose/2-test.md",sourceDirName:"extensions/compose",slug:"/extensions/compose/test",permalink:"/android-testify/docs/1.2.0-alpha01/extensions/compose/test",draft:!1,editUrl:"https://github.com/ndtp/android-testify/tree/issue-12/docusaurus/docs/versioned_docs/version-1.2.0-alpha01/extensions/compose/2-test.md",tags:[],version:"1.2.0-alpha01",sidebarPosition:2,frontMatter:{},sidebar:"tutorialSidebar",previous:{title:"Set up testify-compose",permalink:"/android-testify/docs/1.2.0-alpha01/extensions/compose/setup"},next:{title:"Recipes",permalink:"/android-testify/docs/1.2.0-alpha01/category/recipes"}},p={},u=[],f={toc:u};function m(e){var t=e.components,n=(0,o.Z)(e,a);return(0,s.kt)("wrapper",(0,r.Z)({},f,n,{components:t,mdxType:"MDXLayout"}),(0,s.kt)("h1",{id:"write-a-test"},"Write a test"),(0,s.kt)("p",null,"In order to test a ",(0,s.kt)("inlineCode",{parentName:"p"},"@Composable")," function, you must first declare an instance variable of the ",(0,s.kt)("inlineCode",{parentName:"p"},"ComposableScreenshotRule")," class. Then, you can invoke the ",(0,s.kt)("inlineCode",{parentName:"p"},"setCompose()")," method on the ",(0,s.kt)("inlineCode",{parentName:"p"},"rule")," instance and declare any Compose UI functions you wish to test."),(0,s.kt)("p",null,"Testify will capture only the bounds of the ",(0,s.kt)("inlineCode",{parentName:"p"},"@Composable"),"."),(0,s.kt)("pre",null,(0,s.kt)("code",{parentName:"pre",className:"language-kotlin"},'class ComposableScreenshotTest {\n\n    @get:Rule val rule = ComposableScreenshotRule()\n\n    @ScreenshotInstrumentation\n    @Test\n    fun default() {\n        rule\n            .setCompose {\n                Text(text = "Hello, Testify!")\n            }\n            .assertSame()\n    }\n}\n')))}m.isMDXComponent=!0}}]);