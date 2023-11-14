"use strict";(self.webpackChunkdocs=self.webpackChunkdocs||[]).push([[5550],{3905:(e,t,r)=>{r.d(t,{Zo:()=>u,kt:()=>f});var n=r(7294);function i(e,t,r){return t in e?Object.defineProperty(e,t,{value:r,enumerable:!0,configurable:!0,writable:!0}):e[t]=r,e}function s(e,t){var r=Object.keys(e);if(Object.getOwnPropertySymbols){var n=Object.getOwnPropertySymbols(e);t&&(n=n.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),r.push.apply(r,n)}return r}function c(e){for(var t=1;t<arguments.length;t++){var r=null!=arguments[t]?arguments[t]:{};t%2?s(Object(r),!0).forEach((function(t){i(e,t,r[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(r)):s(Object(r)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(r,t))}))}return e}function o(e,t){if(null==e)return{};var r,n,i=function(e,t){if(null==e)return{};var r,n,i={},s=Object.keys(e);for(n=0;n<s.length;n++)r=s[n],t.indexOf(r)>=0||(i[r]=e[r]);return i}(e,t);if(Object.getOwnPropertySymbols){var s=Object.getOwnPropertySymbols(e);for(n=0;n<s.length;n++)r=s[n],t.indexOf(r)>=0||Object.prototype.propertyIsEnumerable.call(e,r)&&(i[r]=e[r])}return i}var a=n.createContext({}),l=function(e){var t=n.useContext(a),r=t;return e&&(r="function"==typeof e?e(t):c(c({},t),e)),r},u=function(e){var t=l(e.components);return n.createElement(a.Provider,{value:t},e.children)},p="mdxType",d={inlineCode:"code",wrapper:function(e){var t=e.children;return n.createElement(n.Fragment,{},t)}},y=n.forwardRef((function(e,t){var r=e.components,i=e.mdxType,s=e.originalType,a=e.parentName,u=o(e,["components","mdxType","originalType","parentName"]),p=l(r),y=i,f=p["".concat(a,".").concat(y)]||p[y]||d[y]||s;return r?n.createElement(f,c(c({ref:t},u),{},{components:r})):n.createElement(f,c({ref:t},u))}));function f(e,t){var r=arguments,i=t&&t.mdxType;if("string"==typeof e||i){var s=r.length,c=new Array(s);c[0]=y;var o={};for(var a in t)hasOwnProperty.call(t,a)&&(o[a]=t[a]);o.originalType=e,o[p]="string"==typeof e?e:i,c[1]=o;for(var l=2;l<s;l++)c[l]=r[l];return n.createElement.apply(null,c)}return n.createElement.apply(null,r)}y.displayName="MDXCreateElement"},6725:(e,t,r)=>{r.r(t),r.d(t,{assets:()=>a,contentTitle:()=>c,default:()=>p,frontMatter:()=>s,metadata:()=>o,toc:()=>l});var n=r(7462),i=(r(7294),r(3905));const s={},c="Perform accessibility-related checks",o={unversionedId:"extensions/accessibility/test",id:"version-2.0.0-rc02/extensions/accessibility/test",title:"Perform accessibility-related checks",description:"This library collects various accessibility-related checks on View objects as well as AccessibilityNodeInfo objects (which the Android framework derives from Views and sends to AccessibilityServices).",source:"@site/versioned_docs/version-2.0.0-rc02/extensions/accessibility/2-test.md",sourceDirName:"extensions/accessibility",slug:"/extensions/accessibility/test",permalink:"/android-testify/docs/2.0.0-rc02/extensions/accessibility/test",draft:!1,editUrl:"https://github.com/ndtp/android-testify/tree/issue-12/docusaurus/docs/versioned_docs/version-2.0.0-rc02/extensions/accessibility/2-test.md",tags:[],version:"2.0.0-rc02",sidebarPosition:2,frontMatter:{},sidebar:"tutorialSidebar",previous:{title:"Set up testify-accessibility",permalink:"/android-testify/docs/2.0.0-rc02/extensions/accessibility/setup"},next:{title:"Jetpack Compose",permalink:"/android-testify/docs/2.0.0-rc02/category/jetpack-compose"}},a={},l=[{value:"Example",id:"example",level:3}],u={toc:l};function p(e){let{components:t,...r}=e;return(0,i.kt)("wrapper",(0,n.Z)({},u,r,{components:t,mdxType:"MDXLayout"}),(0,i.kt)("h1",{id:"perform-accessibility-related-checks"},"Perform accessibility-related checks"),(0,i.kt)("p",null,"This library collects various accessibility-related checks on View objects as well as AccessibilityNodeInfo objects (which the Android framework derives from Views and sends to AccessibilityServices)."),(0,i.kt)("p",null,"You can use the ",(0,i.kt)("inlineCode",{parentName:"p"},"assertAccessibility()")," method in conjunction with ",(0,i.kt)("inlineCode",{parentName:"p"},"assertSame()")," to simultaneously run accessibilty checks on your screens."),(0,i.kt)("h3",{id:"example"},"Example"),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-kotlin"},"class MainActivityAccessibilityTest {\n\n    @get:Rule\n    val rule = ScreenshotRule(MainActivity::class.java)\n\n    @ScreenshotInstrumentation\n    @Test\n    fun default() {\n        rule\n            .assertAccessibility()\n            .assertSame()\n    }\n}\n")))}p.isMDXComponent=!0}}]);