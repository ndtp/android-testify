"use strict";(self.webpackChunkdocs=self.webpackChunkdocs||[]).push([[3417],{3905:(e,t,n)=>{n.d(t,{Zo:()=>u,kt:()=>f});var r=n(7294);function a(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function o(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);t&&(r=r.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,r)}return n}function i(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?o(Object(n),!0).forEach((function(t){a(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):o(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function l(e,t){if(null==e)return{};var n,r,a=function(e,t){if(null==e)return{};var n,r,a={},o=Object.keys(e);for(r=0;r<o.length;r++)n=o[r],t.indexOf(n)>=0||(a[n]=e[n]);return a}(e,t);if(Object.getOwnPropertySymbols){var o=Object.getOwnPropertySymbols(e);for(r=0;r<o.length;r++)n=o[r],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(a[n]=e[n])}return a}var s=r.createContext({}),c=function(e){var t=r.useContext(s),n=t;return e&&(n="function"==typeof e?e(t):i(i({},t),e)),n},u=function(e){var t=c(e.components);return r.createElement(s.Provider,{value:t},e.children)},p="mdxType",d={inlineCode:"code",wrapper:function(e){var t=e.children;return r.createElement(r.Fragment,{},t)}},m=r.forwardRef((function(e,t){var n=e.components,a=e.mdxType,o=e.originalType,s=e.parentName,u=l(e,["components","mdxType","originalType","parentName"]),p=c(n),m=a,f=p["".concat(s,".").concat(m)]||p[m]||d[m]||o;return n?r.createElement(f,i(i({ref:t},u),{},{components:n})):r.createElement(f,i({ref:t},u))}));function f(e,t){var n=arguments,a=t&&t.mdxType;if("string"==typeof e||a){var o=n.length,i=new Array(o);i[0]=m;var l={};for(var s in t)hasOwnProperty.call(t,s)&&(l[s]=t[s]);l.originalType=e,l[p]="string"==typeof e?e:a,i[1]=l;for(var c=2;c<o;c++)i[c]=n[c];return r.createElement.apply(null,i)}return r.createElement.apply(null,n)}m.displayName="MDXCreateElement"},5162:(e,t,n)=>{n.d(t,{Z:()=>i});var r=n(7294),a=n(6010);const o={tabItem:"tabItem_Ymn6"};function i(e){let{children:t,hidden:n,className:i}=e;return r.createElement("div",{role:"tabpanel",className:(0,a.Z)(o.tabItem,i),hidden:n},t)}},5488:(e,t,n)=>{n.d(t,{Z:()=>d});var r=n(7462),a=n(7294),o=n(6010),i=n(2389),l=n(7392),s=n(7094),c=n(2466);const u={tabList:"tabList__CuJ",tabItem:"tabItem_LNqP"};function p(e){const{lazy:t,block:n,defaultValue:i,values:p,groupId:d,className:m}=e,f=a.Children.map(e.children,(e=>{if((0,a.isValidElement)(e)&&"value"in e.props)return e;throw new Error(`Docusaurus error: Bad <Tabs> child <${"string"==typeof e.type?e.type:e.type.name}>: all children of the <Tabs> component should be <TabItem>, and every <TabItem> should have a unique "value" prop.`)})),b=p??f.map((e=>{let{props:{value:t,label:n,attributes:r}}=e;return{value:t,label:n,attributes:r}})),h=(0,l.l)(b,((e,t)=>e.value===t.value));if(h.length>0)throw new Error(`Docusaurus error: Duplicate values "${h.map((e=>e.value)).join(", ")}" found in <Tabs>. Every value needs to be unique.`);const y=null===i?i:i??f.find((e=>e.props.default))?.props.value??f[0].props.value;if(null!==y&&!b.some((e=>e.value===y)))throw new Error(`Docusaurus error: The <Tabs> has a defaultValue "${y}" but none of its children has the corresponding value. Available values are: ${b.map((e=>e.value)).join(", ")}. If you intend to show no default tab, use defaultValue={null} instead.`);const{tabGroupChoices:v,setTabGroupChoices:g}=(0,s.U)(),[w,k]=(0,a.useState)(y),T=[],{blockElementScrollPositionUntilNextRender:O}=(0,c.o5)();if(null!=d){const e=v[d];null!=e&&e!==w&&b.some((t=>t.value===e))&&k(e)}const E=e=>{const t=e.currentTarget,n=T.indexOf(t),r=b[n].value;r!==w&&(O(t),k(r),null!=d&&g(d,String(r)))},N=e=>{let t=null;switch(e.key){case"ArrowRight":{const n=T.indexOf(e.currentTarget)+1;t=T[n]??T[0];break}case"ArrowLeft":{const n=T.indexOf(e.currentTarget)-1;t=T[n]??T[T.length-1];break}}t?.focus()};return a.createElement("div",{className:(0,o.Z)("tabs-container",u.tabList)},a.createElement("ul",{role:"tablist","aria-orientation":"horizontal",className:(0,o.Z)("tabs",{"tabs--block":n},m)},b.map((e=>{let{value:t,label:n,attributes:i}=e;return a.createElement("li",(0,r.Z)({role:"tab",tabIndex:w===t?0:-1,"aria-selected":w===t,key:t,ref:e=>T.push(e),onKeyDown:N,onFocus:E,onClick:E},i,{className:(0,o.Z)("tabs__item",u.tabItem,i?.className,{"tabs__item--active":w===t})}),n??t)}))),t?(0,a.cloneElement)(f.filter((e=>e.props.value===w))[0],{className:"margin-top--md"}):a.createElement("div",{className:"margin-top--md"},f.map(((e,t)=>(0,a.cloneElement)(e,{key:t,hidden:e.props.value!==w})))))}function d(e){const t=(0,i.Z)();return a.createElement(p,(0,r.Z)({key:String(t)},e))}},9449:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>u,contentTitle:()=>s,default:()=>f,frontMatter:()=>l,metadata:()=>c,toc:()=>p});var r=n(7462),a=(n(7294),n(3905)),o=n(5488),i=n(5162);const l={},s="Changing the orientation of the screen",c={unversionedId:"recipes/orientation",id:"recipes/orientation",title:"Changing the orientation of the screen",description:"Use the orientation configuration to select between portrait and landscape mode.",source:"@site/docs/recipes/10-orientation.md",sourceDirName:"recipes",slug:"/recipes/orientation",permalink:"/android-testify/docs/recipes/orientation",draft:!1,editUrl:"https://github.com/ndtp/android-testify/tree/issue-12/docusaurus/docs/docs/recipes/10-orientation.md",tags:[],version:"current",sidebarPosition:10,frontMatter:{},sidebar:"tutorialSidebar",previous:{title:"Writing a test in Java",permalink:"/android-testify/docs/recipes/java"},next:{title:"Debugging with the Layout Inspector",permalink:"/android-testify/docs/recipes/layout-inspector"}},u={},p=[],d={toc:p},m="wrapper";function f(e){let{components:t,...n}=e;return(0,a.kt)(m,(0,r.Z)({},d,n,{components:t,mdxType:"MDXLayout"}),(0,a.kt)("h1",{id:"changing-the-orientation-of-the-screen"},"Changing the orientation of the screen"),(0,a.kt)("p",null,"Use the ",(0,a.kt)("inlineCode",{parentName:"p"},"orientation")," configuration to select between portrait and landscape mode.\nTestify will use the orientation of the screen as the key for the baseline images. For example, the baseline images for portrait screenshots taken on a Pixel 3a device will be written to the ",(0,a.kt)("inlineCode",{parentName:"p"},"29-1080x2220@440dp-en_US")," directory, while a landscape screenshot taken on the same device will be written to ",(0,a.kt)("inlineCode",{parentName:"p"},"29-2220x1080@440dp-en_US"),"."),(0,a.kt)(o.Z,{mdxType:"Tabs"},(0,a.kt)(i.Z,{value:"rule",label:"ScreenshotTestRule",mdxType:"TabItem"},(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-kotlin"},"@TestifyLayout(R.layout.view_client_details)\n@ScreenshotInstrumentation\n@Test\nfun setOrientation() {\n    rule\n        .configure {\n            orientation = SCREEN_ORIENTATION_LANDSCAPE\n        }\n        .assertSame()\n}\n"))),(0,a.kt)(i.Z,{value:"scenario",label:"ScreenshotScenarioRule",mdxType:"TabItem"},(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-kotlin"},"@TestifyLayout(R.layout.view_client_details)\n@ScreenshotInstrumentation\n@Test\nfun setOrientation() {\n    launchActivity<TestHarnessActivity>().use { scenario ->\n        rule\n            .withScenario(scenario)\n            .configure {\n                orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE\n            }\n            .assertSame()\n    }\n}\n")))))}f.isMDXComponent=!0}}]);