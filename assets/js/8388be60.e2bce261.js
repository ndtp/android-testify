"use strict";(self.webpackChunkdocs=self.webpackChunkdocs||[]).push([[7021],{3905:(e,t,i)=>{i.d(t,{Zo:()=>p,kt:()=>y});var r=i(7294);function s(e,t,i){return t in e?Object.defineProperty(e,t,{value:i,enumerable:!0,configurable:!0,writable:!0}):e[t]=i,e}function o(e,t){var i=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);t&&(r=r.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),i.push.apply(i,r)}return i}function n(e){for(var t=1;t<arguments.length;t++){var i=null!=arguments[t]?arguments[t]:{};t%2?o(Object(i),!0).forEach((function(t){s(e,t,i[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(i)):o(Object(i)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(i,t))}))}return e}function c(e,t){if(null==e)return{};var i,r,s=function(e,t){if(null==e)return{};var i,r,s={},o=Object.keys(e);for(r=0;r<o.length;r++)i=o[r],t.indexOf(i)>=0||(s[i]=e[i]);return s}(e,t);if(Object.getOwnPropertySymbols){var o=Object.getOwnPropertySymbols(e);for(r=0;r<o.length;r++)i=o[r],t.indexOf(i)>=0||Object.prototype.propertyIsEnumerable.call(e,i)&&(s[i]=e[i])}return s}var a=r.createContext({}),l=function(e){var t=r.useContext(a),i=t;return e&&(i="function"==typeof e?e(t):n(n({},t),e)),i},p=function(e){var t=l(e.components);return r.createElement(a.Provider,{value:t},e.children)},d="mdxType",u={inlineCode:"code",wrapper:function(e){var t=e.children;return r.createElement(r.Fragment,{},t)}},b=r.forwardRef((function(e,t){var i=e.components,s=e.mdxType,o=e.originalType,a=e.parentName,p=c(e,["components","mdxType","originalType","parentName"]),d=l(i),b=s,y=d["".concat(a,".").concat(b)]||d[b]||u[b]||o;return i?r.createElement(y,n(n({ref:t},p),{},{components:i})):r.createElement(y,n({ref:t},p))}));function y(e,t){var i=arguments,s=t&&t.mdxType;if("string"==typeof e||s){var o=i.length,n=new Array(o);n[0]=b;var c={};for(var a in t)hasOwnProperty.call(t,a)&&(c[a]=t[a]);c.originalType=e,c[d]="string"==typeof e?e:s,n[1]=c;for(var l=2;l<o;l++)n[l]=i[l];return r.createElement.apply(null,n)}return r.createElement.apply(null,i)}b.displayName="MDXCreateElement"},6246:(e,t,i)=>{i.r(t),i.d(t,{assets:()=>a,contentTitle:()=>n,default:()=>d,frontMatter:()=>o,metadata:()=>c,toc:()=>l});var r=i(7462),s=(i(7294),i(3905));const o={},n="Accessibility Checks Overview",c={unversionedId:"extensions/accessibility/overview",id:"extensions/accessibility/overview",title:"Accessibility Checks Overview",description:"To help people with disabilities access Android apps, developers of those apps need to consider how their apps will be presented to accessibility services. Some good practices can be checked by automated tools, such as if a View has a contentDescription. Other rules require human judgment, such as whether or not a contentDescription makes sense to all users. Testify Accessibility can be used to verify common errors that lead to a poorly accessible application.",source:"@site/docs/extensions/accessibility/0-overview.md",sourceDirName:"extensions/accessibility",slug:"/extensions/accessibility/overview",permalink:"/android-testify/docs/extensions/accessibility/overview",draft:!1,editUrl:"https://github.com/ndtp/android-testify/tree/issue-12/docusaurus/docs/docs/extensions/accessibility/0-overview.md",tags:[],version:"current",sidebarPosition:0,frontMatter:{},sidebar:"tutorialSidebar",previous:{title:"Accessibility Checks",permalink:"/android-testify/docs/category/accessibility-checks"},next:{title:"Set up testify-accessibility",permalink:"/android-testify/docs/extensions/accessibility/setup"}},a={},l=[],p={toc:l};function d(e){let{components:t,...i}=e;return(0,s.kt)("wrapper",(0,r.Z)({},p,i,{components:t,mdxType:"MDXLayout"}),(0,s.kt)("h1",{id:"accessibility-checks-overview"},"Accessibility Checks Overview"),(0,s.kt)("p",null,"To help people with disabilities access Android apps, developers of those apps need to consider how their apps will be presented to accessibility services. Some good practices can be checked by automated tools, such as if a View has a contentDescription. Other rules require human judgment, such as whether or not a contentDescription makes sense to all users. Testify Accessibility can be used to verify common errors that lead to a poorly accessible application."),(0,s.kt)("p",null,"You can combine visual regression testing with accessibility checks to further improve the quality and expand the reach of your application."),(0,s.kt)("p",null,"Enabling ",(0,s.kt)("inlineCode",{parentName:"p"},"assertAccessibility")," on ",(0,s.kt)("inlineCode",{parentName:"p"},"ScreenshotRule")," will run the latest set of checks as defined by the ",(0,s.kt)("a",{parentName:"p",href:"https://github.com/google/Accessibility-Test-Framework-for-Android"},"Accessibility Test Framework for Android"),". This library collects various accessibility-related checks on ",(0,s.kt)("inlineCode",{parentName:"p"},"View")," objects as well as ",(0,s.kt)("inlineCode",{parentName:"p"},"AccessibilityNodeInfo")," objects (which the Android framework derives from Views and sends to AccessibilityServices)."),(0,s.kt)("p",null,"For more information about accessibility, see the ",(0,s.kt)("a",{parentName:"p",href:"https://developer.android.com/guide/topics/ui/accessibility"},"Accessibility guides"),".\nFor more information about ",(0,s.kt)("em",{parentName:"p"},"Mobile Accessibility"),", see: ",(0,s.kt)("a",{parentName:"p",href:"http://www.w3.org/WAI/mobile/"},"http://www.w3.org/WAI/mobile/"),"\nFor more information about ",(0,s.kt)("em",{parentName:"p"},"Accessibility Checking"),", please see ",(0,s.kt)("a",{parentName:"p",href:"https://developer.android.com/training/testing/espresso/accessibility-checking"},"https://developer.android.com/training/testing/espresso/accessibility-checking")),(0,s.kt)("admonition",{type:"info"},(0,s.kt)("p",{parentName:"admonition"},"Testify Accessibility is based on ",(0,s.kt)("a",{parentName:"p",href:"https://github.com/google/Accessibility-Test-Framework-for-Android"},"Accessibility Test Framework for Android")," which currently does not support Compose-based UI.")))}d.isMDXComponent=!0}}]);