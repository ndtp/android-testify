"use strict";(self.webpackChunkdocs=self.webpackChunkdocs||[]).push([[6285],{3905:(e,t,r)=>{r.d(t,{Zo:()=>u,kt:()=>d});var n=r(7294);function a(e,t,r){return t in e?Object.defineProperty(e,t,{value:r,enumerable:!0,configurable:!0,writable:!0}):e[t]=r,e}function o(e,t){var r=Object.keys(e);if(Object.getOwnPropertySymbols){var n=Object.getOwnPropertySymbols(e);t&&(n=n.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),r.push.apply(r,n)}return r}function i(e){for(var t=1;t<arguments.length;t++){var r=null!=arguments[t]?arguments[t]:{};t%2?o(Object(r),!0).forEach((function(t){a(e,t,r[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(r)):o(Object(r)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(r,t))}))}return e}function s(e,t){if(null==e)return{};var r,n,a=function(e,t){if(null==e)return{};var r,n,a={},o=Object.keys(e);for(n=0;n<o.length;n++)r=o[n],t.indexOf(r)>=0||(a[r]=e[r]);return a}(e,t);if(Object.getOwnPropertySymbols){var o=Object.getOwnPropertySymbols(e);for(n=0;n<o.length;n++)r=o[n],t.indexOf(r)>=0||Object.prototype.propertyIsEnumerable.call(e,r)&&(a[r]=e[r])}return a}var l=n.createContext({}),c=function(e){var t=n.useContext(l),r=t;return e&&(r="function"==typeof e?e(t):i(i({},t),e)),r},u=function(e){var t=c(e.components);return n.createElement(l.Provider,{value:t},e.children)},y="mdxType",p={inlineCode:"code",wrapper:function(e){var t=e.children;return n.createElement(n.Fragment,{},t)}},f=n.forwardRef((function(e,t){var r=e.components,a=e.mdxType,o=e.originalType,l=e.parentName,u=s(e,["components","mdxType","originalType","parentName"]),y=c(r),f=a,d=y["".concat(l,".").concat(f)]||y[f]||p[f]||o;return r?n.createElement(d,i(i({ref:t},u),{},{components:r})):n.createElement(d,i({ref:t},u))}));function d(e,t){var r=arguments,a=t&&t.mdxType;if("string"==typeof e||a){var o=r.length,i=new Array(o);i[0]=f;var s={};for(var l in t)hasOwnProperty.call(t,l)&&(s[l]=t[l]);s.originalType=e,s[y]="string"==typeof e?e:a,i[1]=s;for(var c=2;c<o;c++)i[c]=r[c];return n.createElement.apply(null,i)}return n.createElement.apply(null,r)}f.displayName="MDXCreateElement"},6229:(e,t,r)=>{r.r(t),r.d(t,{assets:()=>l,contentTitle:()=>i,default:()=>y,frontMatter:()=>o,metadata:()=>s,toc:()=>c});var n=r(7462),a=(r(7294),r(3905));const o={},i="Using @TestifyLayout with library projects",s={unversionedId:"recipes/testify-layout-library",id:"version-2.0.0-alpha01/recipes/testify-layout-library",title:"Using @TestifyLayout with library projects",description:"The TestifyLayout annotation allows you to specify a layout resource to be automatically loaded into the host Activity for testing.",source:"@site/versioned_docs/version-2.0.0-alpha01/recipes/5-testify-layout-library.md",sourceDirName:"recipes",slug:"/recipes/testify-layout-library",permalink:"/android-testify/docs/2.0.0-alpha01/recipes/testify-layout-library",draft:!1,editUrl:"https://github.com/ndtp/android-testify/tree/issue-12/docusaurus/docs/versioned_docs/version-2.0.0-alpha01/recipes/5-testify-layout-library.md",tags:[],version:"2.0.0-alpha01",sidebarPosition:5,frontMatter:{},sidebar:"tutorialSidebar",previous:{title:"Increase the matching tolerance",permalink:"/android-testify/docs/2.0.0-alpha01/recipes/tolerance"},next:{title:"Passing Intent extras to the Activity under test",permalink:"/android-testify/docs/2.0.0-alpha01/recipes/intents"}},l={},c=[],u={toc:c};function y(e){let{components:t,...r}=e;return(0,a.kt)("wrapper",(0,n.Z)({},u,r,{components:t,mdxType:"MDXLayout"}),(0,a.kt)("h1",{id:"using-testifylayout-with-library-projects"},"Using @TestifyLayout with library projects"),(0,a.kt)("p",null,"The ",(0,a.kt)("inlineCode",{parentName:"p"},"TestifyLayout"),' annotation allows you to specify a layout resource to be automatically loaded into the host Activity for testing.\nUnfortunately R fields are not constants in Android library projects and R.layout resource IDs cannot be used as annotations parameters.\nInstead, you can specify a fully qualified resource name of the form "package:type/entry" as the ',(0,a.kt)("inlineCode",{parentName:"p"},"layoutResName")," argument on ",(0,a.kt)("inlineCode",{parentName:"p"},"TestifyLayout"),"."),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-kotlin"},'class MainActivityScreenshotTest {\n\n    @get:Rule var rule = ScreenshotRule(MainActivity::class.java)\n\n    @TestifyLayout(layoutResName = "dev.testify.sample:layout/view_client_details")\n    @ScreenshotInstrumentation\n    @Test\n    fun default() {\n        rule.assertSame()\n    }\n}\n')))}y.isMDXComponent=!0}}]);