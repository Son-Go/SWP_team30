import * as React from 'react';

declare module 'react' {
  export interface CSSProperties {
    '--src'?: string;
  }
}
