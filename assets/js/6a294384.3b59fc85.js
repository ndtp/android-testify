"use strict";(self.webpackChunkdocs=self.webpackChunkdocs||[]).push([[4007],{3905:(e,t,n)=>{n.d(t,{Zo:()=>u,kt:()=>f});var r=n(7294);function s(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function a(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);t&&(r=r.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,r)}return n}function o(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?a(Object(n),!0).forEach((function(t){s(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):a(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function i(e,t){if(null==e)return{};var n,r,s=function(e,t){if(null==e)return{};var n,r,s={},a=Object.keys(e);for(r=0;r<a.length;r++)n=a[r],t.indexOf(n)>=0||(s[n]=e[n]);return s}(e,t);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(e);for(r=0;r<a.length;r++)n=a[r],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(s[n]=e[n])}return s}var l=r.createContext({}),c=function(e){var t=r.useContext(l),n=t;return e&&(n="function"==typeof e?e(t):o(o({},t),e)),n},u=function(e){var t=c(e.components);return r.createElement(l.Provider,{value:t},e.children)},p="mdxType",d={inlineCode:"code",wrapper:function(e){var t=e.children;return r.createElement(r.Fragment,{},t)}},m=r.forwardRef((function(e,t){var n=e.components,s=e.mdxType,a=e.originalType,l=e.parentName,u=i(e,["components","mdxType","originalType","parentName"]),p=c(n),m=s,f=p["".concat(l,".").concat(m)]||p[m]||d[m]||a;return n?r.createElement(f,o(o({ref:t},u),{},{components:n})):r.createElement(f,o({ref:t},u))}));function f(e,t){var n=arguments,s=t&&t.mdxType;if("string"==typeof e||s){var a=n.length,o=new Array(a);o[0]=m;var i={};for(var l in t)hasOwnProperty.call(t,l)&&(i[l]=t[l]);i.originalType=e,i[p]="string"==typeof e?e:s,o[1]=i;for(var c=2;c<a;c++)o[c]=n[c];return r.createElement.apply(null,o)}return r.createElement.apply(null,n)}m.displayName="MDXCreateElement"},7584:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>l,contentTitle:()=>o,default:()=>d,frontMatter:()=>a,metadata:()=>i,toc:()=>c});var r=n(7462),s=(n(7294),n(3905));const a={},o="Write a full-screen screenshot test",i={unversionedId:"extensions/fullscreen/test",id:"version-2.0.0-rc02/extensions/fullscreen/test",title:"Write a full-screen screenshot test",description:"In order to capture the full device screen, you must set the capture method on ScreenshotRule to fullscreenCapture().",source:"@site/versioned_docs/version-2.0.0-rc02/extensions/fullscreen/2-test.md",sourceDirName:"extensions/fullscreen",slug:"/extensions/fullscreen/test",permalink:"/android-testify/docs/2.0.0-rc02/extensions/fullscreen/test",draft:!1,editUrl:"https://github.com/ndtp/android-testify/tree/issue-12/docusaurus/docs/versioned_docs/version-2.0.0-rc02/extensions/fullscreen/2-test.md",tags:[],version:"2.0.0-rc02",sidebarPosition:2,frontMatter:{},sidebar:"tutorialSidebar",previous:{title:"Set up testify-fullscreen",permalink:"/android-testify/docs/2.0.0-rc02/extensions/fullscreen/setup"},next:{title:"Recipes",permalink:"/android-testify/docs/2.0.0-rc02/category/recipes"}},l={},c=[{value:"Example",id:"example",level:3}],u={toc:c},p="wrapper";function d(e){let{components:t,...n}=e;return(0,s.kt)(p,(0,r.Z)({},u,n,{components:t,mdxType:"MDXLayout"}),(0,s.kt)("h1",{id:"write-a-full-screen-screenshot-test"},"Write a full-screen screenshot test"),(0,s.kt)("p",null,"In order to capture the full device screen, you must set the capture method on ",(0,s.kt)("inlineCode",{parentName:"p"},"ScreenshotRule")," to ",(0,s.kt)("inlineCode",{parentName:"p"},"fullscreenCapture()"),".\nYou can do this with either ",(0,s.kt)("inlineCode",{parentName:"p"},"setCaptureMethod(::fullscreenCapture)")," or the helper extension method ",(0,s.kt)("inlineCode",{parentName:"p"},"captureFullscreen()"),"."),(0,s.kt)("p",null,"Additonal examples can be found in ",(0,s.kt)("a",{parentName:"p",href:"https://github.com/ndtp/android-testify/blob/main/Samples/Legacy/src/androidTest/java/dev/testify/sample/FullscreenCaptureExampleTests.kt"},"FullscreenCaptureExampleTest.kt"),"."),(0,s.kt)("div",{className:"admonition admonition-tip alert alert--success"},(0,s.kt)("div",{parentName:"div",className:"admonition-heading"},(0,s.kt)("h5",{parentName:"div"},(0,s.kt)("span",{parentName:"h5",className:"admonition-icon"},(0,s.kt)("svg",{parentName:"span",xmlns:"http://www.w3.org/2000/svg",width:"12",height:"16",viewBox:"0 0 12 16"},(0,s.kt)("path",{parentName:"svg",fillRule:"evenodd",d:"M6.5 0C3.48 0 1 2.19 1 5c0 .92.55 2.25 1 3 1.34 2.25 1.78 2.78 2 4v1h5v-1c.22-1.22.66-1.75 2-4 .45-.75 1-2.08 1-3 0-2.81-2.48-5-5.5-5zm3.64 7.48c-.25.44-.47.8-.67 1.11-.86 1.41-1.25 2.06-1.45 3.23-.02.05-.02.11-.02.17H5c0-.06 0-.13-.02-.17-.2-1.17-.59-1.83-1.45-3.23-.2-.31-.42-.67-.67-1.11C2.44 6.78 2 5.65 2 5c0-2.2 2.02-4 4.5-4 1.22 0 2.36.42 3.22 1.19C10.55 2.94 11 3.94 11 5c0 .66-.44 1.78-.86 2.48zM4 14h5c-.23 1.14-1.3 2-2.5 2s-2.27-.86-2.5-2z"}))),"tip")),(0,s.kt)("div",{parentName:"div",className:"admonition-content"},(0,s.kt)("p",{parentName:"div"},"The Fullscreen Capture Method will capture system UI, which can include changes out of your control. This includes system notifications, the current time and the network strength indicator.\nIt is frequently desirable to exclude these elements from the comparison. Testify can ignore differences in those elements through the use of the ",(0,s.kt)("inlineCode",{parentName:"p"},"excludeStatusBar()"),", ",(0,s.kt)("inlineCode",{parentName:"p"},"excludeNavigationBar()"),", or ",(0,s.kt)("inlineCode",{parentName:"p"},"excludeSystemUi()")," methods."))),(0,s.kt)("h3",{id:"example"},"Example"),(0,s.kt)("pre",null,(0,s.kt)("code",{parentName:"pre",className:"language-kotlin"},"class FullscreenCaptureTest {\n\n    @get:Rule\n    val rule = ScreenshotRule(MainActivity::class.java)\n\n    @ScreenshotInstrumentation\n    @Test\n    fun fullscreen() {\n        rule\n            .captureFullscreen()    // Set the fullscreen capture method\n            .excludeSystemUi()      // Exclude the navigation bar and status bar areas from the comparison\n            .setExactness(0.95f)    // Allow a 5% variation in color\n            .assertSame()\n    }\n}\n")))}d.isMDXComponent=!0}}]);