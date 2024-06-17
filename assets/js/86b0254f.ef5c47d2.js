"use strict";(self.webpackChunkdocs=self.webpackChunkdocs||[]).push([[5872],{3905:(e,t,n)=>{n.d(t,{Zo:()=>c,kt:()=>f});var a=n(7294);function r(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function s(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(e);t&&(a=a.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,a)}return n}function i(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?s(Object(n),!0).forEach((function(t){r(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):s(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function l(e,t){if(null==e)return{};var n,a,r=function(e,t){if(null==e)return{};var n,a,r={},s=Object.keys(e);for(a=0;a<s.length;a++)n=s[a],t.indexOf(n)>=0||(r[n]=e[n]);return r}(e,t);if(Object.getOwnPropertySymbols){var s=Object.getOwnPropertySymbols(e);for(a=0;a<s.length;a++)n=s[a],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(r[n]=e[n])}return r}var o=a.createContext({}),u=function(e){var t=a.useContext(o),n=t;return e&&(n="function"==typeof e?e(t):i(i({},t),e)),n},c=function(e){var t=u(e.components);return a.createElement(o.Provider,{value:t},e.children)},d="mdxType",p={inlineCode:"code",wrapper:function(e){var t=e.children;return a.createElement(a.Fragment,{},t)}},m=a.forwardRef((function(e,t){var n=e.components,r=e.mdxType,s=e.originalType,o=e.parentName,c=l(e,["components","mdxType","originalType","parentName"]),d=u(n),m=r,f=d["".concat(o,".").concat(m)]||d[m]||p[m]||s;return n?a.createElement(f,i(i({ref:t},c),{},{components:n})):a.createElement(f,i({ref:t},c))}));function f(e,t){var n=arguments,r=t&&t.mdxType;if("string"==typeof e||r){var s=n.length,i=new Array(s);i[0]=m;var l={};for(var o in t)hasOwnProperty.call(t,o)&&(l[o]=t[o]);l.originalType=e,l[d]="string"==typeof e?e:r,i[1]=l;for(var u=2;u<s;u++)i[u]=n[u];return a.createElement.apply(null,i)}return a.createElement.apply(null,n)}m.displayName="MDXCreateElement"},5162:(e,t,n)=>{n.d(t,{Z:()=>i});var a=n(7294),r=n(6010);const s={tabItem:"tabItem_Ymn6"};function i(e){let{children:t,hidden:n,className:i}=e;return a.createElement("div",{role:"tabpanel",className:(0,r.Z)(s.tabItem,i),hidden:n},t)}},5488:(e,t,n)=>{n.d(t,{Z:()=>p});var a=n(7462),r=n(7294),s=n(6010),i=n(2389),l=n(7392),o=n(7094),u=n(2466);const c={tabList:"tabList__CuJ",tabItem:"tabItem_LNqP"};function d(e){const{lazy:t,block:n,defaultValue:i,values:d,groupId:p,className:m}=e,f=r.Children.map(e.children,(e=>{if((0,r.isValidElement)(e)&&"value"in e.props)return e;throw new Error(`Docusaurus error: Bad <Tabs> child <${"string"==typeof e.type?e.type:e.type.name}>: all children of the <Tabs> component should be <TabItem>, and every <TabItem> should have a unique "value" prop.`)})),h=d??f.map((e=>{let{props:{value:t,label:n,attributes:a}}=e;return{value:t,label:n,attributes:a}})),y=(0,l.l)(h,((e,t)=>e.value===t.value));if(y.length>0)throw new Error(`Docusaurus error: Duplicate values "${y.map((e=>e.value)).join(", ")}" found in <Tabs>. Every value needs to be unique.`);const b=null===i?i:i??f.find((e=>e.props.default))?.props.value??f[0].props.value;if(null!==b&&!h.some((e=>e.value===b)))throw new Error(`Docusaurus error: The <Tabs> has a defaultValue "${b}" but none of its children has the corresponding value. Available values are: ${h.map((e=>e.value)).join(", ")}. If you intend to show no default tab, use defaultValue={null} instead.`);const{tabGroupChoices:g,setTabGroupChoices:k}=(0,o.U)(),[v,T]=(0,r.useState)(b),x=[],{blockElementScrollPositionUntilNextRender:w}=(0,u.o5)();if(null!=p){const e=g[p];null!=e&&e!==v&&h.some((t=>t.value===e))&&T(e)}const N=e=>{const t=e.currentTarget,n=x.indexOf(t),a=h[n].value;a!==v&&(w(t),T(a),null!=p&&k(p,String(a)))},E=e=>{let t=null;switch(e.key){case"ArrowRight":{const n=x.indexOf(e.currentTarget)+1;t=x[n]??x[0];break}case"ArrowLeft":{const n=x.indexOf(e.currentTarget)-1;t=x[n]??x[x.length-1];break}}t?.focus()};return r.createElement("div",{className:(0,s.Z)("tabs-container",c.tabList)},r.createElement("ul",{role:"tablist","aria-orientation":"horizontal",className:(0,s.Z)("tabs",{"tabs--block":n},m)},h.map((e=>{let{value:t,label:n,attributes:i}=e;return r.createElement("li",(0,a.Z)({role:"tab",tabIndex:v===t?0:-1,"aria-selected":v===t,key:t,ref:e=>x.push(e),onKeyDown:E,onFocus:N,onClick:N},i,{className:(0,s.Z)("tabs__item",c.tabItem,i?.className,{"tabs__item--active":v===t})}),n??t)}))),t?(0,r.cloneElement)(f.filter((e=>e.props.value===v))[0],{className:"margin-top--md"}):r.createElement("div",{className:"margin-top--md"},f.map(((e,t)=>(0,r.cloneElement)(e,{key:t,hidden:e.props.value!==v})))))}function p(e){const t=(0,i.Z)();return r.createElement(d,(0,a.Z)({key:String(t)},e))}},3969:(e,t,n)=>{n.r(t),n.d(t,{Swatch:()=>p,assets:()=>c,contentTitle:()=>o,default:()=>h,frontMatter:()=>l,metadata:()=>u,toc:()=>d});var a=n(7462),r=(n(7294),n(3905)),s=n(5488),i=n(5162);const l={},o="Verify the tests",u={unversionedId:"get-started/verify-tests",id:"get-started/verify-tests",title:"Verify the tests",description:"You can use Android Studio's built-in test runner to run your tests. Or, you can invoke the gradle task screenshotTest to run all the screenshot tests in your app. The test will fail if any differences from the baseline are detected.",source:"@site/docs/get-started/6-verify-tests.md",sourceDirName:"get-started",slug:"/get-started/verify-tests",permalink:"/android-testify/docs/get-started/verify-tests",draft:!1,editUrl:"https://github.com/ndtp/android-testify/tree/issue-12/docusaurus/docs/docs/get-started/6-verify-tests.md",tags:[],version:"current",sidebarPosition:6,frontMatter:{},sidebar:"tutorialSidebar",previous:{title:"Update your baseline",permalink:"/android-testify/docs/get-started/update-baseline"},next:{title:"Install and use the Android Studio Plugin",permalink:"/android-testify/docs/get-started/set-up-intellij-plugin"}},c={},d=[{value:"Test failures",id:"test-failures",level:2},{value:"Tolerance",id:"tolerance",level:3},{value:"Exclusions",id:"exclusions",level:3},{value:"Diagnosing Differences",id:"diagnosing-differences",level:2},{value:"Additional Testing Scenarios",id:"additional-testing-scenarios",level:2}],p=e=>{let{children:t,color:n}=e;return(0,r.kt)("span",{style:{backgroundColor:n,borderRadius:"2px",color:"#fff",padding:"0.2rem"}},t)},m={toc:d,Swatch:p},f="wrapper";function h(e){let{components:t,...n}=e;return(0,r.kt)(f,(0,a.Z)({},m,n,{components:t,mdxType:"MDXLayout"}),(0,r.kt)("h1",{id:"verify-the-tests"},"Verify the tests"),(0,r.kt)("p",null,"You can use Android Studio's built-in test runner to run your tests. Or, you can invoke the gradle task ",(0,r.kt)("inlineCode",{parentName:"p"},"screenshotTest")," to run all the screenshot tests in your app. The test will fail if any differences from the baseline are detected."),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-shell-session"},"$ ./gradlew app:screenshotTest\n")),(0,r.kt)("h2",{id:"test-failures"},"Test failures"),(0,r.kt)("p",null,"By default, Testify will use a strict binary comparison. This means that any difference in the binary value used for any of the pixels will be considered a failure. You may wish to adjust the matching tolerance through the use of the ",(0,r.kt)("inlineCode",{parentName:"p"},"exactness")," tolerance. A value of less than ",(0,r.kt)("inlineCode",{parentName:"p"},"1.0f")," will result in a more leniant comparison which will exclude visually similar pixels. For more information on Testify's tolerance implementation, please read the blog post ",(0,r.kt)("a",{parentName:"p",href:"../../blog/platform-differences"},(0,r.kt)("em",{parentName:"a"},"Accounting for platform differences")),"."),(0,r.kt)("h3",{id:"tolerance"},"Tolerance"),(0,r.kt)("p",null,"To adjust the tolerance, configure the ",(0,r.kt)("inlineCode",{parentName:"p"},"exactness")," value on the rule or you can use the ",(0,r.kt)("inlineCode",{parentName:"p"},"@BitmapComparisonExactness"),"."),(0,r.kt)(s.Z,{mdxType:"Tabs"},(0,r.kt)(i.Z,{value:"setExactness",label:"configure",mdxType:"TabItem"},(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-kotlin"},"@ScreenshotInstrumentation\n@Test\nfun setExactness() {\n    rule\n        .configure { exactness = 0.95f }\n        .assertSame()\n}\n"))),(0,r.kt)(i.Z,{value:"bitmapComparisonExactness",label:"@BitmapComparisonExactness",mdxType:"TabItem"},(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-kotlin"},"import dev.testify.annotation.BitmapComparisonExactness\n\n@ScreenshotInstrumentation\n@BitmapComparisonExactness(exactness = 0.95f)\n@Test\nfun setExactness() {\n    rule.assertSame()\n}\n")))),(0,r.kt)("h3",{id:"exclusions"},"Exclusions"),(0,r.kt)("p",null,"In addition to adjusting the tolerance, you can also exclude certain parts of the screen from comparion. You can define ",(0,r.kt)("em",{parentName:"p"},"exclusion rects")," which Testify will ignore when comparing images."),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-kotlin"},"@ScreenshotInstrumentation\n@Test\nfun exclusions() {\n    rule\n        .configure {\n            defineExclusionRects { rootView, exclusionRects ->\n                val card = rootView.findViewById<View>(R.id.info_card)\n                exclusionRects.add(card.boundingBox)\n            }\n        }\n        .assertSame()\n}\n")),(0,r.kt)("h2",{id:"diagnosing-differences"},"Diagnosing Differences"),(0,r.kt)("p",null,"When a test fails, it can sometimes be difficult to determine the cause. You can enable the ",(0,r.kt)("em",{parentName:"p"},"GenerateDiffs")," feature which will write a companion image for your screenshot test which can help you more easily identify which areas of your test have triggered the screenshot failure."),(0,r.kt)("p",null,"The generated file will be created in the same directory as your baseline images. Diff files can be pulled from the device using ",(0,r.kt)("inlineCode",{parentName:"p"},"./gradlew app:screenshotPull"),"."),(0,r.kt)("ul",null,(0,r.kt)("li",{parentName:"ul"},(0,r.kt)(p,{color:"#000000",mdxType:"Swatch"},"\xa0\xa0\xa0\xa0\xa0")," Black pixels are identical between the baseline and test image"),(0,r.kt)("li",{parentName:"ul"},(0,r.kt)(p,{color:"#888888",mdxType:"Swatch"},"\xa0\xa0\xa0\xa0\xa0")," Grey pixels have been excluded from the comparison"),(0,r.kt)("li",{parentName:"ul"},(0,r.kt)(p,{color:"#FFFF00",mdxType:"Swatch"},"\xa0\xa0\xa0\xa0\xa0")," Yellow pixels are different, but within the Exactness threshold"),(0,r.kt)("li",{parentName:"ul"},(0,r.kt)(p,{color:"#FF0000",mdxType:"Swatch"},"\xa0\xa0\xa0\xa0\xa0")," Red pixels are different")),(0,r.kt)("p",null,"This feature can be enabled by adding the ",(0,r.kt)("inlineCode",{parentName:"p"},"testify-generate-diffs")," tag to the ",(0,r.kt)("inlineCode",{parentName:"p"},"AndroidManifest.xml")," file in your ",(0,r.kt)("inlineCode",{parentName:"p"},"androidTest")," target:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-xml"},'<?xml version="1.0" encoding="utf-8"?>\n<manifest xmlns:android="http://schemas.android.com/apk/res/android">\n    <application>\n        <meta-data\n            android:name="testify-generate-diffs"\n            android:value="true" />\n    </application>\n</manifest>\n')),(0,r.kt)("p",null,"Alternatively, you can enable/disable diffs programmatically:"),(0,r.kt)(s.Z,{mdxType:"Tabs"},(0,r.kt)(i.Z,{value:"TestifyFeatures",label:"TestifyFeatures",mdxType:"TabItem"},(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-kotlin"},"@ScreenshotInstrumentation\n@Test\nfun generateDiffs() {\n    TestifyFeatures.GenerateDiffs.setEnabled(true)\n    rule.assertSame()\n}\n"))),(0,r.kt)(i.Z,{value:"withExperimentalFeatureEnabled",label:"withExperimentalFeatureEnabled",mdxType:"TabItem"},(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-kotlin"},"@ScreenshotInstrumentation\n@Test\nfun generateDiffs() {\n    rule\n        .withExperimentalFeatureEnabled(TestifyFeatures.GenerateDiffs)\n        .assertSame()\n}\n")))),(0,r.kt)("h2",{id:"additional-testing-scenarios"},"Additional Testing Scenarios"),(0,r.kt)("p",null,"For additional examples and advanced testing scenarios, please check out ",(0,r.kt)("a",{parentName:"p",href:"../category/recipes"},"Testify Recipes"),"."),(0,r.kt)("p",null,"Testify is built on top of ",(0,r.kt)("a",{parentName:"p",href:"https://developer.android.com/training/testing/instrumented-tests"},"Android Instrumented Tests")," and so you can also you any of Android's built-in instrumentation test running mechanisms to verify your tests."),(0,r.kt)("p",null,"You can:"),(0,r.kt)("ul",null,(0,r.kt)("li",{parentName:"ul"},(0,r.kt)("a",{parentName:"li",href:"https://developer.android.com/studio/test"},"Test from Android Studio")),(0,r.kt)("li",{parentName:"ul"},(0,r.kt)("a",{parentName:"li",href:"https://developer.android.com/studio/test/command-line"},"Test from the command line"))))}h.isMDXComponent=!0}}]);