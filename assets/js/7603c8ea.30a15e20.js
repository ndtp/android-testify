"use strict";(self.webpackChunkdocs=self.webpackChunkdocs||[]).push([[3509],{3905:(e,t,n)=>{n.d(t,{Zo:()=>u,kt:()=>m});var r=n(7294);function s(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function o(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);t&&(r=r.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,r)}return n}function a(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?o(Object(n),!0).forEach((function(t){s(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):o(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function l(e,t){if(null==e)return{};var n,r,s=function(e,t){if(null==e)return{};var n,r,s={},o=Object.keys(e);for(r=0;r<o.length;r++)n=o[r],t.indexOf(n)>=0||(s[n]=e[n]);return s}(e,t);if(Object.getOwnPropertySymbols){var o=Object.getOwnPropertySymbols(e);for(r=0;r<o.length;r++)n=o[r],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(s[n]=e[n])}return s}var i=r.createContext({}),c=function(e){var t=r.useContext(i),n=t;return e&&(n="function"==typeof e?e(t):a(a({},t),e)),n},u=function(e){var t=c(e.components);return r.createElement(i.Provider,{value:t},e.children)},p="mdxType",d={inlineCode:"code",wrapper:function(e){var t=e.children;return r.createElement(r.Fragment,{},t)}},f=r.forwardRef((function(e,t){var n=e.components,s=e.mdxType,o=e.originalType,i=e.parentName,u=l(e,["components","mdxType","originalType","parentName"]),p=c(n),f=s,m=p["".concat(i,".").concat(f)]||p[f]||d[f]||o;return n?r.createElement(m,a(a({ref:t},u),{},{components:n})):r.createElement(m,a({ref:t},u))}));function m(e,t){var n=arguments,s=t&&t.mdxType;if("string"==typeof e||s){var o=n.length,a=new Array(o);a[0]=f;var l={};for(var i in t)hasOwnProperty.call(t,i)&&(l[i]=t[i]);l.originalType=e,l[p]="string"==typeof e?e:s,a[1]=l;for(var c=2;c<o;c++)a[c]=n[c];return r.createElement.apply(null,a)}return r.createElement.apply(null,n)}f.displayName="MDXCreateElement"},7322:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>i,contentTitle:()=>a,default:()=>p,frontMatter:()=>o,metadata:()=>l,toc:()=>c});var r=n(7462),s=(n(7294),n(3905));const o={},a="Write a full-screen screenshot test",l={unversionedId:"extensions/fullscreen/test",id:"extensions/fullscreen/test",title:"Write a full-screen screenshot test",description:"In order to capture the full device screen, you must set the capture method on ScreenshotRule to fullscreenCapture().",source:"@site/docs/extensions/fullscreen/2-test.md",sourceDirName:"extensions/fullscreen",slug:"/extensions/fullscreen/test",permalink:"/android-testify/docs/extensions/fullscreen/test",draft:!1,editUrl:"https://github.com/ndtp/android-testify/tree/issue-12/docusaurus/docs/docs/extensions/fullscreen/2-test.md",tags:[],version:"current",sidebarPosition:2,frontMatter:{},sidebar:"tutorialSidebar",previous:{title:"Set up testify-fullscreen",permalink:"/android-testify/docs/extensions/fullscreen/setup"},next:{title:"Recipes",permalink:"/android-testify/docs/category/recipes"}},i={},c=[{value:"Example",id:"example",level:3}],u={toc:c};function p(e){let{components:t,...n}=e;return(0,s.kt)("wrapper",(0,r.Z)({},u,n,{components:t,mdxType:"MDXLayout"}),(0,s.kt)("h1",{id:"write-a-full-screen-screenshot-test"},"Write a full-screen screenshot test"),(0,s.kt)("p",null,"In order to capture the full device screen, you must set the capture method on ",(0,s.kt)("inlineCode",{parentName:"p"},"ScreenshotRule")," to ",(0,s.kt)("inlineCode",{parentName:"p"},"fullscreenCapture()"),".\nYou can do this with either ",(0,s.kt)("inlineCode",{parentName:"p"},"setCaptureMethod(::fullscreenCapture)")," or the helper extension method ",(0,s.kt)("inlineCode",{parentName:"p"},"captureFullscreen()"),"."),(0,s.kt)("p",null,"Additonal examples can be found in ",(0,s.kt)("a",{parentName:"p",href:"https://github.com/ndtp/android-testify/blob/main/Sample/src/androidTest/java/dev/testify/sample/FullscreenCaptureExampleTests.kt"},"FullscreenCaptureExampleTest.kt"),"."),(0,s.kt)("admonition",{type:"tip"},(0,s.kt)("p",{parentName:"admonition"},"The Fullscreen Capture Method will capture system UI, which can include changes out of your control. This includes system notifications, the current time and the network strength indicator.\nIt is frequently desirable to exclude these elements from the comparison. Testify can ignore differences in those elements through the use of the ",(0,s.kt)("inlineCode",{parentName:"p"},"excludeStatusBar()"),", ",(0,s.kt)("inlineCode",{parentName:"p"},"excludeNavigationBar()"),", or ",(0,s.kt)("inlineCode",{parentName:"p"},"excludeSystemUi()")," methods.")),(0,s.kt)("h3",{id:"example"},"Example"),(0,s.kt)("pre",null,(0,s.kt)("code",{parentName:"pre",className:"language-kotlin"},"class FullscreenCaptureTest {\n\n    @get:Rule\n    val rule = ScreenshotRule(MainActivity::class.java)\n\n    @ScreenshotInstrumentation\n    @Test\n    fun fullscreen() {\n        rule\n            .captureFullscreen()    // Set the fullscreen capture method\n            .excludeSystemUi()      // Exclude the navigation bar and status bar areas from the comparison\n            .setExactness(0.95f)    // Allow a 5% variation in color\n            .assertSame()\n    }\n}\n")))}p.isMDXComponent=!0}}]);