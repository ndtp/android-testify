"use strict";(self.webpackChunkdocs=self.webpackChunkdocs||[]).push([[2231],{3905:(e,t,n)=>{n.d(t,{Zo:()=>p,kt:()=>y});var r=n(7294);function o(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function a(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);t&&(r=r.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,r)}return n}function i(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?a(Object(n),!0).forEach((function(t){o(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):a(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function s(e,t){if(null==e)return{};var n,r,o=function(e,t){if(null==e)return{};var n,r,o={},a=Object.keys(e);for(r=0;r<a.length;r++)n=a[r],t.indexOf(n)>=0||(o[n]=e[n]);return o}(e,t);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(e);for(r=0;r<a.length;r++)n=a[r],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(o[n]=e[n])}return o}var u=r.createContext({}),c=function(e){var t=r.useContext(u),n=t;return e&&(n="function"==typeof e?e(t):i(i({},t),e)),n},p=function(e){var t=c(e.components);return r.createElement(u.Provider,{value:t},e.children)},l="mdxType",d={inlineCode:"code",wrapper:function(e){var t=e.children;return r.createElement(r.Fragment,{},t)}},h=r.forwardRef((function(e,t){var n=e.components,o=e.mdxType,a=e.originalType,u=e.parentName,p=s(e,["components","mdxType","originalType","parentName"]),l=c(n),h=o,y=l["".concat(u,".").concat(h)]||l[h]||d[h]||a;return n?r.createElement(y,i(i({ref:t},p),{},{components:n})):r.createElement(y,i({ref:t},p))}));function y(e,t){var n=arguments,o=t&&t.mdxType;if("string"==typeof e||o){var a=n.length,i=new Array(a);i[0]=h;var s={};for(var u in t)hasOwnProperty.call(t,u)&&(s[u]=t[u]);s.originalType=e,s[l]="string"==typeof e?e:o,i[1]=s;for(var c=2;c<a;c++)i[c]=n[c];return r.createElement.apply(null,i)}return r.createElement.apply(null,n)}h.displayName="MDXCreateElement"},2962:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>u,contentTitle:()=>i,default:()=>l,frontMatter:()=>a,metadata:()=>s,toc:()=>c});var r=n(7462),o=(n(7294),n(3905));const a={},i="Debugging with the Layout Inspector",s={unversionedId:"recipes/layout-inspector",id:"version-1.2.0-alpha01/recipes/layout-inspector",title:"Debugging with the Layout Inspector",description:"You may use Android Studio's Layout Inspector in conjunction with your screenshot test. It can sometimes be useful to pause your test so that you can capture the layout hierarchy for further debugging in Android Studio. In order to do so, invoke the setLayoutInspectionModeEnabled method on the test rule. This will pause the test after all ViewModifications have been applied and prior to the screenshot being taken. The test is paused for 5 minutes, allowing plenty of time to capture the layout.",source:"@site/versioned_docs/version-1.2.0-alpha01/recipes/11-layout-inspector.md",sourceDirName:"recipes",slug:"/recipes/layout-inspector",permalink:"/android-testify/docs/1.2.0-alpha01/recipes/layout-inspector",draft:!1,editUrl:"https://github.com/ndtp/android-testify/tree/issue-12/docusaurus/docs/versioned_docs/version-1.2.0-alpha01/recipes/11-layout-inspector.md",tags:[],version:"1.2.0-alpha01",sidebarPosition:11,frontMatter:{},sidebar:"tutorialSidebar",previous:{title:"Changing the orientation of the screen",permalink:"/android-testify/docs/1.2.0-alpha01/recipes/orientation"},next:{title:"Selecting an alternative capture method",permalink:"/android-testify/docs/1.2.0-alpha01/recipes/capture-method"}},u={},c=[],p={toc:c};function l(e){let{components:t,...n}=e;return(0,o.kt)("wrapper",(0,r.Z)({},p,n,{components:t,mdxType:"MDXLayout"}),(0,o.kt)("h1",{id:"debugging-with-the-layout-inspector"},"Debugging with the Layout Inspector"),(0,o.kt)("p",null,"You may use Android Studio's ",(0,o.kt)("a",{parentName:"p",href:"https://developer.android.com/studio/debug/layout-inspector"},"Layout Inspector")," in conjunction with your screenshot test. It can sometimes be useful to pause your test so that you can capture the layout hierarchy for further debugging in Android Studio. In order to do so, invoke the ",(0,o.kt)("inlineCode",{parentName:"p"},"setLayoutInspectionModeEnabled")," method on the test rule. This will pause the test after all ViewModifications have been applied and prior to the screenshot being taken. The test is paused for 5 minutes, allowing plenty of time to capture the layout."),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-kotlin"},"    @ScreenshotInstrumentation\n    @Test\n    fun testDefault() {\n        rule\n                .setLayoutInspectionModeEnabled(true)\n                .assertSame()\n    }\n")))}l.isMDXComponent=!0}}]);