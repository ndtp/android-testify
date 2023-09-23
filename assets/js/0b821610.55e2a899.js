"use strict";(self.webpackChunkdocs=self.webpackChunkdocs||[]).push([[7210],{3905:(e,r,t)=>{t.d(r,{Zo:()=>l,kt:()=>m});var n=t(7294);function o(e,r,t){return r in e?Object.defineProperty(e,r,{value:t,enumerable:!0,configurable:!0,writable:!0}):e[r]=t,e}function a(e,r){var t=Object.keys(e);if(Object.getOwnPropertySymbols){var n=Object.getOwnPropertySymbols(e);r&&(n=n.filter((function(r){return Object.getOwnPropertyDescriptor(e,r).enumerable}))),t.push.apply(t,n)}return t}function i(e){for(var r=1;r<arguments.length;r++){var t=null!=arguments[r]?arguments[r]:{};r%2?a(Object(t),!0).forEach((function(r){o(e,r,t[r])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(t)):a(Object(t)).forEach((function(r){Object.defineProperty(e,r,Object.getOwnPropertyDescriptor(t,r))}))}return e}function s(e,r){if(null==e)return{};var t,n,o=function(e,r){if(null==e)return{};var t,n,o={},a=Object.keys(e);for(n=0;n<a.length;n++)t=a[n],r.indexOf(t)>=0||(o[t]=e[t]);return o}(e,r);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(e);for(n=0;n<a.length;n++)t=a[n],r.indexOf(t)>=0||Object.prototype.propertyIsEnumerable.call(e,t)&&(o[t]=e[t])}return o}var c=n.createContext({}),d=function(e){var r=n.useContext(c),t=r;return e&&(t="function"==typeof e?e(r):i(i({},r),e)),t},l=function(e){var r=d(e.components);return n.createElement(c.Provider,{value:r},e.children)},u="mdxType",f={inlineCode:"code",wrapper:function(e){var r=e.children;return n.createElement(n.Fragment,{},r)}},p=n.forwardRef((function(e,r){var t=e.components,o=e.mdxType,a=e.originalType,c=e.parentName,l=s(e,["components","mdxType","originalType","parentName"]),u=d(t),p=o,m=u["".concat(c,".").concat(p)]||u[p]||f[p]||a;return t?n.createElement(m,i(i({ref:r},l),{},{components:t})):n.createElement(m,i({ref:r},l))}));function m(e,r){var t=arguments,o=r&&r.mdxType;if("string"==typeof e||o){var a=t.length,i=new Array(a);i[0]=p;var s={};for(var c in r)hasOwnProperty.call(r,c)&&(s[c]=r[c]);s.originalType=e,s[u]="string"==typeof e?e:o,i[1]=s;for(var d=2;d<a;d++)i[d]=t[d];return n.createElement.apply(null,i)}return n.createElement.apply(null,t)}p.displayName="MDXCreateElement"},6917:(e,r,t)=>{t.r(r),t.d(r,{assets:()=>c,contentTitle:()=>i,default:()=>u,frontMatter:()=>a,metadata:()=>s,toc:()=>d});var n=t(7462),o=(t(7294),t(3905));const a={},i="Force software rendering",s={unversionedId:"recipes/software-rendering",id:"version-2.0.0-beta03/recipes/software-rendering",title:"Force software rendering",description:"In some instances it may be desirable to use the software renderer, not Android's default hardware renderer. Differences in GPU hardware from device to device (and emulators running on different architectures) may cause flakiness in rendering.",source:"@site/versioned_docs/version-2.0.0-beta03/recipes/13-software-rendering.md",sourceDirName:"recipes",slug:"/recipes/software-rendering",permalink:"/android-testify/docs/2.0.0-beta03/recipes/software-rendering",draft:!1,editUrl:"https://github.com/ndtp/android-testify/tree/issue-12/docusaurus/docs/versioned_docs/version-2.0.0-beta03/recipes/13-software-rendering.md",tags:[],version:"2.0.0-beta03",sidebarPosition:13,frontMatter:{},sidebar:"tutorialSidebar",previous:{title:"Selecting an alternative capture method",permalink:"/android-testify/docs/2.0.0-beta03/recipes/capture-method"},next:{title:"Excluding a region from the comparison",permalink:"/android-testify/docs/2.0.0-beta03/recipes/exclude-regions"}},c={},d=[],l={toc:d};function u(e){let{components:r,...t}=e;return(0,o.kt)("wrapper",(0,n.Z)({},l,t,{components:r,mdxType:"MDXLayout"}),(0,o.kt)("h1",{id:"force-software-rendering"},"Force software rendering"),(0,o.kt)("p",null,"In some instances it may be desirable to use the software renderer, not Android's default hardware renderer. Differences in GPU hardware from device to device (and emulators running on different architectures) may cause flakiness in rendering."),(0,o.kt)("p",null,"Please read more about ",(0,o.kt)("a",{parentName:"p",href:"https://developer.android.com/guide/topics/graphics/hardware-accel.html"},"Hardware acceleration")," for more information."),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-kotlin"},"    @ScreenshotInstrumentation\n    @Test\n    fun default() {\n        rule\n            .setUseSoftwareRenderer(true)\n            .assertSame()\n    }\n")))}u.isMDXComponent=!0}}]);