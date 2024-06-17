"use strict";(self.webpackChunkdocs=self.webpackChunkdocs||[]).push([[2820],{3905:(e,t,r)=>{r.d(t,{Zo:()=>c,kt:()=>f});var n=r(7294);function a(e,t,r){return t in e?Object.defineProperty(e,t,{value:r,enumerable:!0,configurable:!0,writable:!0}):e[t]=r,e}function i(e,t){var r=Object.keys(e);if(Object.getOwnPropertySymbols){var n=Object.getOwnPropertySymbols(e);t&&(n=n.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),r.push.apply(r,n)}return r}function o(e){for(var t=1;t<arguments.length;t++){var r=null!=arguments[t]?arguments[t]:{};t%2?i(Object(r),!0).forEach((function(t){a(e,t,r[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(r)):i(Object(r)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(r,t))}))}return e}function l(e,t){if(null==e)return{};var r,n,a=function(e,t){if(null==e)return{};var r,n,a={},i=Object.keys(e);for(n=0;n<i.length;n++)r=i[n],t.indexOf(r)>=0||(a[r]=e[r]);return a}(e,t);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);for(n=0;n<i.length;n++)r=i[n],t.indexOf(r)>=0||Object.prototype.propertyIsEnumerable.call(e,r)&&(a[r]=e[r])}return a}var u=n.createContext({}),s=function(e){var t=n.useContext(u),r=t;return e&&(r="function"==typeof e?e(t):o(o({},t),e)),r},c=function(e){var t=s(e.components);return n.createElement(u.Provider,{value:t},e.children)},p="mdxType",d={inlineCode:"code",wrapper:function(e){var t=e.children;return n.createElement(n.Fragment,{},t)}},m=n.forwardRef((function(e,t){var r=e.components,a=e.mdxType,i=e.originalType,u=e.parentName,c=l(e,["components","mdxType","originalType","parentName"]),p=s(r),m=a,f=p["".concat(u,".").concat(m)]||p[m]||d[m]||i;return r?n.createElement(f,o(o({ref:t},c),{},{components:r})):n.createElement(f,o({ref:t},c))}));function f(e,t){var r=arguments,a=t&&t.mdxType;if("string"==typeof e||a){var i=r.length,o=new Array(i);o[0]=m;var l={};for(var u in t)hasOwnProperty.call(t,u)&&(l[u]=t[u]);l.originalType=e,l[p]="string"==typeof e?e:a,o[1]=l;for(var s=2;s<i;s++)o[s]=r[s];return n.createElement.apply(null,o)}return n.createElement.apply(null,r)}m.displayName="MDXCreateElement"},5270:(e,t,r)=>{r.r(t),r.d(t,{assets:()=>u,contentTitle:()=>o,default:()=>d,frontMatter:()=>i,metadata:()=>l,toc:()=>s});var n=r(7462),a=(r(7294),r(3905));const i={},o="Configure your emulator to run Testify tests",l={unversionedId:"get-started/configuring-an-emulator",id:"version-2.0.0/get-started/configuring-an-emulator",title:"Configure your emulator to run Testify tests",description:"The Sample application includes a baseline for an emulator that's compatible with GitHub Actions. To configure an AVD locally, create a new virtual device with the following settings in the Android Virtual Device (AVD) configuration:",source:"@site/versioned_docs/version-2.0.0/get-started/2-configuring-an-emulator.md",sourceDirName:"get-started",slug:"/get-started/configuring-an-emulator",permalink:"/android-testify/docs/2.0.0/get-started/configuring-an-emulator",draft:!1,editUrl:"https://github.com/ndtp/android-testify/tree/issue-12/docusaurus/docs/versioned_docs/version-2.0.0/get-started/2-configuring-an-emulator.md",tags:[],version:"2.0.0",sidebarPosition:2,frontMatter:{},sidebar:"tutorialSidebar",previous:{title:"Set up an Android application project to use Testify",permalink:"/android-testify/docs/2.0.0/get-started/setup"},next:{title:"Write a test",permalink:"/android-testify/docs/2.0.0/get-started/write-a-test"}},u={},s=[{value:"Intel based hardware",id:"intel-based-hardware",level:2},{value:"Apple M1 hardware",id:"apple-m1-hardware",level:2}],c={toc:s},p="wrapper";function d(e){let{components:t,...r}=e;return(0,a.kt)(p,(0,n.Z)({},c,r,{components:t,mdxType:"MDXLayout"}),(0,a.kt)("h1",{id:"configure-your-emulator-to-run-testify-tests"},"Configure your emulator to run Testify tests"),(0,a.kt)("p",null,"The Sample application includes a baseline for an emulator that's compatible with GitHub Actions. To configure an AVD locally, create a new virtual device with the following settings in the Android Virtual Device (AVD) configuration:"),(0,a.kt)("h2",{id:"intel-based-hardware"},"Intel based hardware"),(0,a.kt)("ul",null,(0,a.kt)("li",{parentName:"ul"},"Phone: Pixel 3a (1080x2220 440dpi)"),(0,a.kt)("li",{parentName:"ul"},"Q API level 29, x86, Android 10.0 (Google APIs)"),(0,a.kt)("li",{parentName:"ul"},"RAM: 1536 MB"),(0,a.kt)("li",{parentName:"ul"},"VM heap: 256 MB"),(0,a.kt)("li",{parentName:"ul"},"Internal Storage: 2048 MB"),(0,a.kt)("li",{parentName:"ul"},"SD card, Studio-managed: 512 MB"),(0,a.kt)("li",{parentName:"ul"},"Enable Device Frame with pixel_3a skin"),(0,a.kt)("li",{parentName:"ul"},"Enable keyboard input")),(0,a.kt)("h2",{id:"apple-m1-hardware"},"Apple M1 hardware"),(0,a.kt)("ul",null,(0,a.kt)("li",{parentName:"ul"},"Phone: Pixel 3a (1080x2220 440dpi)"),(0,a.kt)("li",{parentName:"ul"},"Q API level 29, ",(0,a.kt)("strong",{parentName:"li"},"arm64"),", Android 10.0 (Google APIs)"),(0,a.kt)("li",{parentName:"ul"},"RAM: 1536 MB"),(0,a.kt)("li",{parentName:"ul"},"VM heap: 256 MB"),(0,a.kt)("li",{parentName:"ul"},"Internal Storage: 2048 MB"),(0,a.kt)("li",{parentName:"ul"},"SD card, Studio-managed: 512 MB"),(0,a.kt)("li",{parentName:"ul"},"Enable Device Frame with pixel_3a skin"),(0,a.kt)("li",{parentName:"ul"},"Enable keyboard input")),(0,a.kt)("p",null,"Once the emulator is booted:"),(0,a.kt)("ul",null,(0,a.kt)("li",{parentName:"ul"},"Set the Language to English (United States) (",(0,a.kt)("inlineCode",{parentName:"li"},"en_US"),")"),(0,a.kt)("li",{parentName:"ul"},"In the developer settings, set Window animation scale, Transition animation scale, and Animator duration scale to ",(0,a.kt)("inlineCode",{parentName:"li"},"off"))))}d.isMDXComponent=!0}}]);