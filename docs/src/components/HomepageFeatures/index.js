import React from 'react';
import clsx from 'clsx';
import styles from './styles.module.css';

const FeatureList = [
  {
    title: 'Seamless Integration',
    Svg: require('@site/static/img/seamless.svg').default,
    description: (
      <>
        Quickly and easily integrate screenshot tests into your existing instrumented Android tests.
      </>
    ),
  },
  {
    title: 'Catch Visual Regressions',
    Svg: require('@site/static/img/regression.svg').default,
    description: (
      <>
        Monitor and track changes to the visual presentation of your application. Catch unintended modifications to the layout of your screens.
      </>
    ),
  },
  {
    title: 'Versatile',
    Svg: require('@site/static/img/versatile.svg').default,
    description: (
      <>
        Easily capture accurate screenshots of your application, whether you use traditional Android View hierarchies or Jetpack Compose-based UI.
      </>
    ),
  },
];

function Feature({Svg, title, description}) {
  return (
    <div className={clsx('col col--4')}>
      <div className="text--center">
        <Svg className={styles.featureSvg} role="img" />
      </div>
      <div className="text--center padding-horiz--md">
        <h3>{title}</h3>
        <p>{description}</p>
      </div>
    </div>
  );
}

export default function HomepageFeatures() {
  return (
    <section className={styles.features}>
      <div className="container">
        <div className="row">
          {FeatureList.map((props, idx) => (
            <Feature key={idx} {...props} />
          ))}
        </div>
      </div>
    </section>
  );
}
