"use strict";(self.webpackChunkdocs=self.webpackChunkdocs||[]).push([[4064],{3905:(e,t,r)=>{r.d(t,{Zo:()=>u,kt:()=>m});var n=r(7294);function s(e,t,r){return t in e?Object.defineProperty(e,t,{value:r,enumerable:!0,configurable:!0,writable:!0}):e[t]=r,e}function i(e,t){var r=Object.keys(e);if(Object.getOwnPropertySymbols){var n=Object.getOwnPropertySymbols(e);t&&(n=n.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),r.push.apply(r,n)}return r}function a(e){for(var t=1;t<arguments.length;t++){var r=null!=arguments[t]?arguments[t]:{};t%2?i(Object(r),!0).forEach((function(t){s(e,t,r[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(r)):i(Object(r)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(r,t))}))}return e}function o(e,t){if(null==e)return{};var r,n,s=function(e,t){if(null==e)return{};var r,n,s={},i=Object.keys(e);for(n=0;n<i.length;n++)r=i[n],t.indexOf(r)>=0||(s[r]=e[r]);return s}(e,t);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);for(n=0;n<i.length;n++)r=i[n],t.indexOf(r)>=0||Object.prototype.propertyIsEnumerable.call(e,r)&&(s[r]=e[r])}return s}var c=n.createContext({}),l=function(e){var t=n.useContext(c),r=t;return e&&(r="function"==typeof e?e(t):a(a({},t),e)),r},u=function(e){var t=l(e.components);return n.createElement(c.Provider,{value:t},e.children)},p="mdxType",d={inlineCode:"code",wrapper:function(e){var t=e.children;return n.createElement(n.Fragment,{},t)}},f=n.forwardRef((function(e,t){var r=e.components,s=e.mdxType,i=e.originalType,c=e.parentName,u=o(e,["components","mdxType","originalType","parentName"]),p=l(r),f=s,m=p["".concat(c,".").concat(f)]||p[f]||d[f]||i;return r?n.createElement(m,a(a({ref:t},u),{},{components:r})):n.createElement(m,a({ref:t},u))}));function m(e,t){var r=arguments,s=t&&t.mdxType;if("string"==typeof e||s){var i=r.length,a=new Array(i);a[0]=f;var o={};for(var c in t)hasOwnProperty.call(t,c)&&(o[c]=t[c]);o.originalType=e,o[p]="string"==typeof e?e:s,a[1]=o;for(var l=2;l<i;l++)a[l]=r[l];return n.createElement.apply(null,a)}return n.createElement.apply(null,r)}f.displayName="MDXCreateElement"},8867:(e,t,r)=>{r.r(t),r.d(t,{assets:()=>c,contentTitle:()=>a,default:()=>d,frontMatter:()=>i,metadata:()=>o,toc:()=>l});var n=r(7462),s=(r(7294),r(3905));const i={},a="Fullscreen Capture Method Overview",o={unversionedId:"extensions/fullscreen/overview",id:"extensions/fullscreen/overview",title:"Fullscreen Capture Method Overview",description:"Test your app as your user sees it",source:"@site/docs/extensions/fullscreen/0-overview.md",sourceDirName:"extensions/fullscreen",slug:"/extensions/fullscreen/overview",permalink:"/android-testify/docs/extensions/fullscreen/overview",draft:!1,editUrl:"https://github.com/ndtp/android-testify/tree/issue-12/docusaurus/docs/docs/extensions/fullscreen/0-overview.md",tags:[],version:"current",sidebarPosition:0,frontMatter:{},sidebar:"tutorialSidebar",previous:{title:"Fullscreen Capture Method",permalink:"/android-testify/docs/category/fullscreen-capture-method"},next:{title:"Set up testify-fullscreen",permalink:"/android-testify/docs/extensions/fullscreen/setup"}},c={},l=[{value:"Test your app as your user sees it",id:"test-your-app-as-your-user-sees-it",level:3},{value:"Capture the entire device screen, including system UI, dialogs and menus",id:"capture-the-entire-device-screen-including-system-ui-dialogs-and-menus",level:3},{value:"How it works",id:"how-it-works",level:3}],u={toc:l},p="wrapper";function d(e){let{components:t,...r}=e;return(0,s.kt)(p,(0,n.Z)({},u,r,{components:t,mdxType:"MDXLayout"}),(0,s.kt)("h1",{id:"fullscreen-capture-method-overview"},"Fullscreen Capture Method Overview"),(0,s.kt)("h3",{id:"test-your-app-as-your-user-sees-it"},"Test your app as your user sees it"),(0,s.kt)("p",null,"Taking a full-screen screenshot can provide a more comprehensive view of the user experience, including system UI elements such as notifications, status bar, and navigation buttons. A full-screen screenshot can help identify bugs that may be related to system UI elements or interactions with the operating system. A full-screen screenshot can help identify compatibility issues with different Android versions, screen sizes, and device manufacturers, including how the app interacts with system UI elements."),(0,s.kt)("p",null,"A full-screen screenshot can provide valuable insights into the app's behavior and help developers improve the app's performance, compatibility, and user experience."),(0,s.kt)("h3",{id:"capture-the-entire-device-screen-including-system-ui-dialogs-and-menus"},"Capture the entire device screen, including system UI, dialogs and menus"),(0,s.kt)("p",null,"The Testify Fullscreen Capture method can be used to capture UI elements presented outside of your root view. This includes elements rendered in a different ",(0,s.kt)("a",{parentName:"p",href:"https://developer.android.com/reference/android/view/Window"},"Window")," such as dialogs, alerts, notifications, or overlays."),(0,s.kt)("h3",{id:"how-it-works"},"How it works"),(0,s.kt)("p",null,"Testify Fullscreen Capture Method uses ",(0,s.kt)("a",{parentName:"p",href:"https://developer.android.com/training/testing/other-components/ui-automator"},"UiAutomator's")," built-in ",(0,s.kt)("a",{parentName:"p",href:"https://developer.android.com/reference/androidx/test/uiautomator/UiDevice#takescreenshot"},"screenshotting")," capability to capture a ",(0,s.kt)("a",{parentName:"p",href:"https://developer.android.com/reference/android/graphics/Bitmap"},"Bitmap")," of the entire device."),(0,s.kt)("p",null,"The bitmap will be generated from a PNG at 1:1 scale and 100% quality. The bitmap's size will match the full device resolution and include all system UI such as the status bar and navigation bar."),(0,s.kt)("p",null,"As the system UI content is highly variable, you can use ",(0,s.kt)("a",{parentName:"p",href:"https://github.com/ndtp/android-testify/tree/main/Ext/Fullscreen/src/main/java/dev/testify/capture/fullscreen/provider/StatusBarExclusionRectProvider.kt"},"ScreenshotRule.excludeStatusBar")," and/or ",(0,s.kt)("a",{parentName:"p",href:"https://github.com/ndtp/android-testify/tree/main/Ext/Fullscreen/src/main/java/dev/testify/capture/fullscreen/provider/NavigationBarExclusionRectProvider.kt"},"ScreenshotRule.excludeNavigationBar")," to ignore the status bar and navigation bar, respectively."),(0,s.kt)("p",null,"Though the PNG is intended to be lossless, some compression artifacts or GPU-related variance can occur. As such, it is recommended to use a small tolerance when capturing fullscreen images."),(0,s.kt)("p",null,"You can set a comparison tolerance using ",(0,s.kt)("a",{parentName:"p",href:"https://github.com/ndtp/android-testify/blob/main/Library/src/main/java/dev/testify/internal/TestifyConfiguration.kt"},"TestifyConfiguration.exactness"),"."))}d.isMDXComponent=!0}}]);