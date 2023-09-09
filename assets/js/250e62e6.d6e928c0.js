"use strict";(self.webpackChunkdocs=self.webpackChunkdocs||[]).push([[9321],{3905:(e,t,r)=>{r.d(t,{Zo:()=>c,kt:()=>m});var n=r(7294);function i(e,t,r){return t in e?Object.defineProperty(e,t,{value:r,enumerable:!0,configurable:!0,writable:!0}):e[t]=r,e}function o(e,t){var r=Object.keys(e);if(Object.getOwnPropertySymbols){var n=Object.getOwnPropertySymbols(e);t&&(n=n.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),r.push.apply(r,n)}return r}function u(e){for(var t=1;t<arguments.length;t++){var r=null!=arguments[t]?arguments[t]:{};t%2?o(Object(r),!0).forEach((function(t){i(e,t,r[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(r)):o(Object(r)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(r,t))}))}return e}function s(e,t){if(null==e)return{};var r,n,i=function(e,t){if(null==e)return{};var r,n,i={},o=Object.keys(e);for(n=0;n<o.length;n++)r=o[n],t.indexOf(r)>=0||(i[r]=e[r]);return i}(e,t);if(Object.getOwnPropertySymbols){var o=Object.getOwnPropertySymbols(e);for(n=0;n<o.length;n++)r=o[n],t.indexOf(r)>=0||Object.prototype.propertyIsEnumerable.call(e,r)&&(i[r]=e[r])}return i}var l=n.createContext({}),a=function(e){var t=n.useContext(l),r=t;return e&&(r="function"==typeof e?e(t):u(u({},t),e)),r},c=function(e){var t=a(e.components);return n.createElement(l.Provider,{value:t},e.children)},p="mdxType",d={inlineCode:"code",wrapper:function(e){var t=e.children;return n.createElement(n.Fragment,{},t)}},f=n.forwardRef((function(e,t){var r=e.components,i=e.mdxType,o=e.originalType,l=e.parentName,c=s(e,["components","mdxType","originalType","parentName"]),p=a(r),f=i,m=p["".concat(l,".").concat(f)]||p[f]||d[f]||o;return r?n.createElement(m,u(u({ref:t},c),{},{components:r})):n.createElement(m,u({ref:t},c))}));function m(e,t){var r=arguments,i=t&&t.mdxType;if("string"==typeof e||i){var o=r.length,u=new Array(o);u[0]=f;var s={};for(var l in t)hasOwnProperty.call(t,l)&&(s[l]=t[l]);s.originalType=e,s[p]="string"==typeof e?e:i,u[1]=s;for(var a=2;a<o;a++)u[a]=r[a];return n.createElement.apply(null,u)}return n.createElement.apply(null,r)}f.displayName="MDXCreateElement"},7385:(e,t,r)=>{r.d(t,{Z:()=>o});var n=r(7294);function i(){return i=Object.assign?Object.assign.bind():function(e){for(var t=1;t<arguments.length;t++){var r=arguments[t];for(var n in r)Object.prototype.hasOwnProperty.call(r,n)&&(e[n]=r[n])}return e},i.apply(this,arguments)}const o=e=>{let{title:t,titleId:r,...o}=e;return n.createElement("svg",i({xmlns:"http://www.w3.org/2000/svg",width:16,height:16,viewBox:"0 0 16 16","aria-labelledby":r},o),t?n.createElement("title",{id:r},t):null,n.createElement("path",{style:{stroke:"none",fillRule:"nonzero",fill:"#9e80b5",fillOpacity:1},d:"M3 14a.947.947 0 0 1-.7-.3.947.947 0 0 1-.3-.7V3c0-.266.098-.5.3-.7.2-.202.434-.3.7-.3h4.652v1H3v10h10V8.348h1V13c0 .266-.098.5-.3.7-.2.202-.434.3-.7.3Zm3.367-3.652-.699-.715L12.301 3H8.652V2H14v5.348h-1V3.715Zm0 0"}))}},1601:(e,t,r)=>{r.r(t),r.d(t,{assets:()=>a,contentTitle:()=>s,default:()=>d,frontMatter:()=>u,metadata:()=>l,toc:()=>c});var n=r(7462),i=(r(7294),r(3905)),o=r(7385);const u={},s="Multi-user support",l={unversionedId:"recipes/multi-user",id:"recipes/multi-user",title:"Multi-user support",description:"Testify provides support for emulators running in multi-user configuration.",source:"@site/docs/recipes/19-multi-user.md",sourceDirName:"recipes",slug:"/recipes/multi-user",permalink:"/android-testify/docs/recipes/multi-user",draft:!1,editUrl:"https://github.com/ndtp/android-testify/tree/issue-12/docusaurus/docs/docs/recipes/19-multi-user.md",tags:[],version:"current",sidebarPosition:19,frontMatter:{},sidebar:"tutorialSidebar",previous:{title:"Configuring Testify to write to the SDCard",permalink:"/android-testify/docs/recipes/sdcard"},next:{title:"Migrate to Testify 2.0",permalink:"/android-testify/docs/migration"}},a={},c=[{value:"Background",id:"background",level:2},{value:"Automatic user selection",id:"automatic-user-selection",level:2},{value:"Configuring the Gradle Plugin",id:"configuring-the-gradle-plugin",level:3}],p={toc:c};function d(e){let{components:t,...r}=e;return(0,i.kt)("wrapper",(0,n.Z)({},p,r,{components:t,mdxType:"MDXLayout"}),(0,i.kt)("h1",{id:"multi-user-support"},"Multi-user support"),(0,i.kt)("p",null,"Testify provides support for emulators running in multi-user configuration."),(0,i.kt)("h2",{id:"background"},"Background"),(0,i.kt)("p",null,(0,i.kt)("a",{parentName:"p",href:"https://source.android.com/docs/devices/admin/multi-user-testing"},"This page ",(0,i.kt)(o.Z,{mdxType:"OpenNew"}))," describes important aspects of testing multiple users on the Android platform. For information about implementing multi-user support, see ",(0,i.kt)("a",{parentName:"p",href:"https://source.android.com/docs/devices/admin/multi-user"},"Supporting Multiple Users ",(0,i.kt)(o.Z,{mdxType:"OpenNew"})),"."),(0,i.kt)("h2",{id:"automatic-user-selection"},"Automatic user selection"),(0,i.kt)("p",null,"Testify automatically reads and writes files to the currently running user. The Testify plugin will correctly pull screenshots from the current user."),(0,i.kt)("h3",{id:"configuring-the-gradle-plugin"},"Configuring the Gradle Plugin"),(0,i.kt)("p",null,"You may optionally configure the Gradle Plugin to pull screenshots from a different user. You can override the user by specifying the ",(0,i.kt)("inlineCode",{parentName:"p"},"user=<number>")," argument."),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre"},"./gradlew app:screenshotPull -Puser=10\n\n")))}d.isMDXComponent=!0}}]);