import React from 'react';
import cn from 'classnames';

import styles from './index.module.scss';

interface IProps {
  className?: string;
}

function LoginButton(props: IProps) {
  return (
    <div className={cn(styles.block24, props.className, 'login-button')}>
      <div className={styles.info12}>Login</div>
    </div>
  );
}

export default LoginButton;
