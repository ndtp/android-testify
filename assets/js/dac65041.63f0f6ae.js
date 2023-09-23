"use strict";(self.webpackChunkdocs=self.webpackChunkdocs||[]).push([[6594],{3905:(e,t,r)=>{r.d(t,{Zo:()=>l,kt:()=>y});var n=r(7294);function o(e,t,r){return t in e?Object.defineProperty(e,t,{value:r,enumerable:!0,configurable:!0,writable:!0}):e[t]=r,e}function i(e,t){var r=Object.keys(e);if(Object.getOwnPropertySymbols){var n=Object.getOwnPropertySymbols(e);t&&(n=n.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),r.push.apply(r,n)}return r}function a(e){for(var t=1;t<arguments.length;t++){var r=null!=arguments[t]?arguments[t]:{};t%2?i(Object(r),!0).forEach((function(t){o(e,t,r[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(r)):i(Object(r)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(r,t))}))}return e}function s(e,t){if(null==e)return{};var r,n,o=function(e,t){if(null==e)return{};var r,n,o={},i=Object.keys(e);for(n=0;n<i.length;n++)r=i[n],t.indexOf(r)>=0||(o[r]=e[r]);return o}(e,t);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);for(n=0;n<i.length;n++)r=i[n],t.indexOf(r)>=0||Object.prototype.propertyIsEnumerable.call(e,r)&&(o[r]=e[r])}return o}var c=n.createContext({}),p=function(e){var t=n.useContext(c),r=t;return e&&(r="function"==typeof e?e(t):a(a({},t),e)),r},l=function(e){var t=p(e.components);return n.createElement(c.Provider,{value:t},e.children)},u="mdxType",d={inlineCode:"code",wrapper:function(e){var t=e.children;return n.createElement(n.Fragment,{},t)}},f=n.forwardRef((function(e,t){var r=e.components,o=e.mdxType,i=e.originalType,c=e.parentName,l=s(e,["components","mdxType","originalType","parentName"]),u=p(r),f=o,y=u["".concat(c,".").concat(f)]||u[f]||d[f]||i;return r?n.createElement(y,a(a({ref:t},l),{},{components:r})):n.createElement(y,a({ref:t},l))}));function y(e,t){var r=arguments,o=t&&t.mdxType;if("string"==typeof e||o){var i=r.length,a=new Array(i);a[0]=f;var s={};for(var c in t)hasOwnProperty.call(t,c)&&(s[c]=t[c]);s.originalType=e,s[u]="string"==typeof e?e:o,a[1]=s;for(var p=2;p<i;p++)a[p]=r[p];return n.createElement.apply(null,a)}return n.createElement.apply(null,r)}f.displayName="MDXCreateElement"},6167:(e,t,r)=>{r.r(t),r.d(t,{assets:()=>c,contentTitle:()=>a,default:()=>u,frontMatter:()=>i,metadata:()=>s,toc:()=>p});var n=r(7462),o=(r(7294),r(3905));const i={},a="Taking a screenshot of an area less than that of the entire Activity",s={unversionedId:"recipes/view-provider",id:"version-2.0.0-beta03/recipes/view-provider",title:"Taking a screenshot of an area less than that of the entire Activity",description:"It is often desirable to capture only a portion of your screen or to capture a single View.",source:"@site/versioned_docs/version-2.0.0-beta03/recipes/1-view-provider.md",sourceDirName:"recipes",slug:"/recipes/view-provider",permalink:"/android-testify/docs/2.0.0-beta03/recipes/view-provider",draft:!1,editUrl:"https://github.com/ndtp/android-testify/tree/issue-12/docusaurus/docs/versioned_docs/version-2.0.0-beta03/recipes/1-view-provider.md",tags:[],version:"2.0.0-beta03",sidebarPosition:1,frontMatter:{},sidebar:"tutorialSidebar",previous:{title:"Recipes",permalink:"/android-testify/docs/2.0.0-beta03/category/recipes"},next:{title:"Changing the Locale in a test",permalink:"/android-testify/docs/2.0.0-beta03/recipes/locale"}},c={},p=[],l={toc:p};function u(e){let{components:t,...r}=e;return(0,o.kt)("wrapper",(0,n.Z)({},l,r,{components:t,mdxType:"MDXLayout"}),(0,o.kt)("h1",{id:"taking-a-screenshot-of-an-area-less-than-that-of-the-entire-activity"},"Taking a screenshot of an area less than that of the entire Activity"),(0,o.kt)("p",null,"It is often desirable to capture only a portion of your screen or to capture a single ",(0,o.kt)("inlineCode",{parentName:"p"},"View"),".\nFor these cases, you can use the ",(0,o.kt)("inlineCode",{parentName:"p"},"setScreenshotViewProvider")," on ",(0,o.kt)("inlineCode",{parentName:"p"},"ScreenshotRule")," to specify which ",(0,o.kt)("inlineCode",{parentName:"p"},"View")," to capture."),(0,o.kt)("p",null,"Using ",(0,o.kt)("inlineCode",{parentName:"p"},"ScreenshotRule.setScreenshotViewProvider"),", you must return a ",(0,o.kt)("inlineCode",{parentName:"p"},"View")," reference which will be used by Testify to narrow the bitmap to only that View."),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-kotlin"},"    @TestifyLayout(R.layout.view_client_details)\n    @ScreenshotInstrumentation\n    @Test\n    fun default() {\n        rule\n            .setScreenshotViewProvider {\n                it.findViewById(R.id.info_card)\n            }\n            .assertSame()\n    }\n")))}u.isMDXComponent=!0}}]);