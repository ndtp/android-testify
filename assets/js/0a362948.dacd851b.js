"use strict";(self.webpackChunkdocs=self.webpackChunkdocs||[]).push([[5655],{3905:(e,t,r)=>{r.d(t,{Zo:()=>u,kt:()=>f});var n=r(7294);function a(e,t,r){return t in e?Object.defineProperty(e,t,{value:r,enumerable:!0,configurable:!0,writable:!0}):e[t]=r,e}function o(e,t){var r=Object.keys(e);if(Object.getOwnPropertySymbols){var n=Object.getOwnPropertySymbols(e);t&&(n=n.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),r.push.apply(r,n)}return r}function i(e){for(var t=1;t<arguments.length;t++){var r=null!=arguments[t]?arguments[t]:{};t%2?o(Object(r),!0).forEach((function(t){a(e,t,r[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(r)):o(Object(r)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(r,t))}))}return e}function s(e,t){if(null==e)return{};var r,n,a=function(e,t){if(null==e)return{};var r,n,a={},o=Object.keys(e);for(n=0;n<o.length;n++)r=o[n],t.indexOf(r)>=0||(a[r]=e[r]);return a}(e,t);if(Object.getOwnPropertySymbols){var o=Object.getOwnPropertySymbols(e);for(n=0;n<o.length;n++)r=o[n],t.indexOf(r)>=0||Object.prototype.propertyIsEnumerable.call(e,r)&&(a[r]=e[r])}return a}var c=n.createContext({}),l=function(e){var t=n.useContext(c),r=t;return e&&(r="function"==typeof e?e(t):i(i({},t),e)),r},u=function(e){var t=l(e.components);return n.createElement(c.Provider,{value:t},e.children)},p="mdxType",y={inlineCode:"code",wrapper:function(e){var t=e.children;return n.createElement(n.Fragment,{},t)}},d=n.forwardRef((function(e,t){var r=e.components,a=e.mdxType,o=e.originalType,c=e.parentName,u=s(e,["components","mdxType","originalType","parentName"]),p=l(r),d=a,f=p["".concat(c,".").concat(d)]||p[d]||y[d]||o;return r?n.createElement(f,i(i({ref:t},u),{},{components:r})):n.createElement(f,i({ref:t},u))}));function f(e,t){var r=arguments,a=t&&t.mdxType;if("string"==typeof e||a){var o=r.length,i=new Array(o);i[0]=d;var s={};for(var c in t)hasOwnProperty.call(t,c)&&(s[c]=t[c]);s.originalType=e,s[p]="string"==typeof e?e:a,i[1]=s;for(var l=2;l<o;l++)i[l]=r[l];return n.createElement.apply(null,i)}return n.createElement.apply(null,r)}d.displayName="MDXCreateElement"},7226:(e,t,r)=>{r.r(t),r.d(t,{assets:()=>c,contentTitle:()=>i,default:()=>p,frontMatter:()=>o,metadata:()=>s,toc:()=>l});var n=r(7462),a=(r(7294),r(3905));const o={},i="Specifying a layout resource programmatically",s={unversionedId:"recipes/layout-resource",id:"version-2.0.0-alpha02/recipes/layout-resource",title:"Specifying a layout resource programmatically",description:"As an alternative to using the TestifyLayout annotation, you may also specific a layout file to be loaded programmatically.",source:"@site/versioned_docs/version-2.0.0-alpha02/recipes/7-layout-resource.md",sourceDirName:"recipes",slug:"/recipes/layout-resource",permalink:"/android-testify/docs/2.0.0-alpha02/recipes/layout-resource",draft:!1,editUrl:"https://github.com/ndtp/android-testify/tree/issue-12/docusaurus/docs/versioned_docs/version-2.0.0-alpha02/recipes/7-layout-resource.md",tags:[],version:"2.0.0-alpha02",sidebarPosition:7,frontMatter:{},sidebar:"tutorialSidebar",previous:{title:"Passing Intent extras to the Activity under test",permalink:"/android-testify/docs/2.0.0-alpha02/recipes/intents"},next:{title:"Use Espresso UI tests with Testify",permalink:"/android-testify/docs/2.0.0-alpha02/recipes/espresso"}},c={},l=[],u={toc:l};function p(e){let{components:t,...r}=e;return(0,a.kt)("wrapper",(0,n.Z)({},u,r,{components:t,mdxType:"MDXLayout"}),(0,a.kt)("h1",{id:"specifying-a-layout-resource-programmatically"},"Specifying a layout resource programmatically"),(0,a.kt)("p",null,"As an alternative to using the ",(0,a.kt)("inlineCode",{parentName:"p"},"TestifyLayout")," annotation, you may also specific a layout file to be loaded programmatically.\nYou can pass a ",(0,a.kt)("inlineCode",{parentName:"p"},"R.layout.*")," resource ID to ",(0,a.kt)("inlineCode",{parentName:"p"},"setTargetLayoutId")," on the ",(0,a.kt)("inlineCode",{parentName:"p"},"ScreenshotRule"),"."),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-kotlin"},"class MainActivityScreenshotTest {\n\n    @get:Rule var rule = ScreenshotRule(MainActivity::class.java)\n\n    @ScreenshotInstrumentation\n    @Test\n    fun default() {\n        rule\n            .setTargetLayoutId(R.layout.view_client_details)\n            .assertSame()\n    }\n}\n")))}p.isMDXComponent=!0}}]);