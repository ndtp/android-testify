"use strict";(self.webpackChunkdocs=self.webpackChunkdocs||[]).push([[7250],{3905:(e,t,r)=>{r.d(t,{Zo:()=>u,kt:()=>y});var n=r(7294);function o(e,t,r){return t in e?Object.defineProperty(e,t,{value:r,enumerable:!0,configurable:!0,writable:!0}):e[t]=r,e}function a(e,t){var r=Object.keys(e);if(Object.getOwnPropertySymbols){var n=Object.getOwnPropertySymbols(e);t&&(n=n.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),r.push.apply(r,n)}return r}function i(e){for(var t=1;t<arguments.length;t++){var r=null!=arguments[t]?arguments[t]:{};t%2?a(Object(r),!0).forEach((function(t){o(e,t,r[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(r)):a(Object(r)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(r,t))}))}return e}function s(e,t){if(null==e)return{};var r,n,o=function(e,t){if(null==e)return{};var r,n,o={},a=Object.keys(e);for(n=0;n<a.length;n++)r=a[n],t.indexOf(r)>=0||(o[r]=e[r]);return o}(e,t);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(e);for(n=0;n<a.length;n++)r=a[n],t.indexOf(r)>=0||Object.prototype.propertyIsEnumerable.call(e,r)&&(o[r]=e[r])}return o}var c=n.createContext({}),p=function(e){var t=n.useContext(c),r=t;return e&&(r="function"==typeof e?e(t):i(i({},t),e)),r},u=function(e){var t=p(e.components);return n.createElement(c.Provider,{value:t},e.children)},l="mdxType",d={inlineCode:"code",wrapper:function(e){var t=e.children;return n.createElement(n.Fragment,{},t)}},f=n.forwardRef((function(e,t){var r=e.components,o=e.mdxType,a=e.originalType,c=e.parentName,u=s(e,["components","mdxType","originalType","parentName"]),l=p(r),f=o,y=l["".concat(c,".").concat(f)]||l[f]||d[f]||a;return r?n.createElement(y,i(i({ref:t},u),{},{components:r})):n.createElement(y,i({ref:t},u))}));function y(e,t){var r=arguments,o=t&&t.mdxType;if("string"==typeof e||o){var a=r.length,i=new Array(a);i[0]=f;var s={};for(var c in t)hasOwnProperty.call(t,c)&&(s[c]=t[c]);s.originalType=e,s[l]="string"==typeof e?e:o,i[1]=s;for(var p=2;p<a;p++)i[p]=r[p];return n.createElement.apply(null,i)}return n.createElement.apply(null,r)}f.displayName="MDXCreateElement"},249:(e,t,r)=>{r.r(t),r.d(t,{assets:()=>c,contentTitle:()=>i,default:()=>l,frontMatter:()=>a,metadata:()=>s,toc:()=>p});var n=r(7462),o=(r(7294),r(3905));const a={},i="Placing the keyboard focus on a specific view",s={unversionedId:"recipes/keyboard-focus",id:"version-1.2.0-alpha01/recipes/keyboard-focus",title:"Placing the keyboard focus on a specific view",description:"It's possible that users can navigate your app using a keyboard, because the Android system enables most of the necessary behaviors by default.",source:"@site/versioned_docs/version-1.2.0-alpha01/recipes/15-keyboard-focus.md",sourceDirName:"recipes",slug:"/recipes/keyboard-focus",permalink:"/android-testify/docs/1.2.0-alpha01/recipes/keyboard-focus",draft:!1,editUrl:"https://github.com/ndtp/android-testify/tree/issue-12/docusaurus/docs/versioned_docs/version-1.2.0-alpha01/recipes/15-keyboard-focus.md",tags:[],version:"1.2.0-alpha01",sidebarPosition:15,frontMatter:{},sidebar:"tutorialSidebar",previous:{title:"Excluding a region from the comparison",permalink:"/android-testify/docs/1.2.0-alpha01/recipes/exclude-regions"},next:{title:"Customizing the captured bitmap",permalink:"/android-testify/docs/1.2.0-alpha01/recipes/custom-bitmap"}},c={},p=[],u={toc:p};function l(e){let{components:t,...r}=e;return(0,o.kt)("wrapper",(0,n.Z)({},u,r,{components:t,mdxType:"MDXLayout"}),(0,o.kt)("h1",{id:"placing-the-keyboard-focus-on-a-specific-view"},"Placing the keyboard focus on a specific view"),(0,o.kt)("p",null,"It's possible that users can navigate your app using a keyboard, because the Android system enables most of the necessary behaviors by default.\nIn order to place the keyboard focus on a specific View, use the ",(0,o.kt)("inlineCode",{parentName:"p"},"setFocusTarget")," method."),(0,o.kt)("p",null,"See ",(0,o.kt)("a",{parentName:"p",href:"https://developer.android.com/training/keyboard-input/navigation"},"https://developer.android.com/training/keyboard-input/navigation")),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-kotlin"},"    @ScreenshotInstrumentation\n    @Test\n    fun default() {\n        rule\n            .setFocusTarget(enabled = true, focusTargetId = R.id.fab)\n            .assertSame()\n    }\n")))}l.isMDXComponent=!0}}]);