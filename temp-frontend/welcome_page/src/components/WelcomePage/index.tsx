import React from 'react';
import cn from 'classnames';
import Login_button from 'components/Login_button';

import styles from './index.module.scss';

interface IProps {
  className?: string;
}

function WelcomePage(props: IProps) {
  return (
    <div className={cn(styles.root, props.className, 'welcome-page')}>
      <div className={styles.block}>
        <div className={styles.wrapper}>
          <div className={styles.wrapper1}>
            <div className={styles.wrapper2}>
              <div className={styles.wrapper3}>
                <img
                  className={styles.image}
                  src={'/assets/WelcomePage/gd_logo_variant.png'}
                  alt="alt text"
                />

                <div className={styles.block2}>
                  <div className={styles.info}>GD Evenings</div>
                </div>
              </div>
            </div>

            <div className={styles.wrapper4}>
              <div className={styles.block3}>
                <div className={styles.info1}>About Club</div>
              </div>

              <div className={styles.wrapper5}>
                <div className={styles.info2}>Games</div>
              </div>

              <div className={styles.wrapper6}>
                <div className={styles.info21}>Events</div>
              </div>

              <div className={styles.wrapper7}>
                <div className={styles.info2}>Forum</div>
              </div>

              <div className={styles.wrapper8}>
                <div className={styles.info21}>Store</div>
              </div>
            </div>

            <div className={styles.wrapper9}>
              <div className={styles.wrapper10}>
                <div className={styles.wrapper11}>
                  <img
                    className={styles.image1}
                    src={'/assets/WelcomePage/bell_icon.svg'}
                    alt="alt text"
                  />
                  <img
                    className={styles.image11}
                    src={'/assets/WelcomePage/profile_icon.svg'}
                    alt="alt text"
                  />
                </div>
              </div>

              <Login_button className={styles.block24} />
            </div>
          </div>
        </div>
      </div>

      <div className={styles.wrapper12}>
        <div className={styles.wrapper13}>
          <div className={styles.wrapper14}>
            <div className={styles.block4}>
              <div className={styles.wrapper15}>
                <div className={styles.color} />

                <div className={styles.wrapper16}>
                  <div className={styles.info3}>{`STATUS: ONLINE & READY`}</div>
                </div>
              </div>
            </div>

            <div className={styles.wrapper17}>
              <p className={styles.desc_box}>
                <span className={styles.desc}>
                  <span className={styles.desc_span0}>
                    Experience Gaming
                    <br />
                  </span>
                  <span className={styles.desc_span1}>After Dark</span>
                </span>
              </p>
            </div>

            <div className={styles.wrapper18}>
              <p className={styles.desc1}>
                Сообщество с ивентами Иннополиса и Казани для всех тех, кто
                интересуется геймдевом. Организуем ивенты, рассматриваем инди
                новинки.
              </p>
            </div>

            <div className={styles.wrapper19}>
              <div className={styles.wrapper20}>
                <div className={styles.block5}>
                  <div className={styles.wrapper21}>
                    <div className={styles.info4}>Заказать ивент</div>
                    <img
                      className={styles.image12}
                      src={'/assets/WelcomePage/calendar_icon.svg'}
                      alt="alt text"
                    />
                  </div>
                </div>

                <div className={styles.block6}>
                  <div className={styles.info11}>Contact Us</div>
                </div>
              </div>
            </div>

            <div className={styles.block7}>
              <div className={styles.wrapper22}>
                <div className={styles.wrapper23}>
                  <div className={styles.wrapper24}>
                    <div className={styles.info5}>1.2k+</div>
                  </div>

                  <div className={styles.wrapper24}>
                    <div className={styles.info6}>Active Members</div>
                  </div>
                </div>

                <div className={styles.wrapper25}>
                  <div className={styles.wrapper24}>
                    <div className={styles.info51}>45+</div>
                  </div>

                  <div className={styles.wrapper24}>
                    <div className={styles.info61}>Weekly Tournaments</div>
                  </div>
                </div>

                <div className={styles.wrapper26}>
                  <div className={styles.wrapper24}>
                    <div className={styles.info52}>24/7</div>
                  </div>

                  <div className={styles.wrapper24}>
                    <div className={styles.info61}>Lounge Access</div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div className={styles.wrapper27}>
            <img
              className={styles.decorator}
              src={'/assets/WelcomePage/gaming_setup.png'}
              alt="alt text"
            />

            <div className={styles.block9}>
              <div className={styles.wrapper28}>
                <img
                  className={styles.image3}
                  src={'/assets/WelcomePage/rocket_icon.svg'}
                  alt="alt text"
                />

                <div className={styles.wrapper29}>
                  <div className={styles.wrapper24}>
                    <div className={styles.info7}>Next Event</div>
                  </div>

                  <div className={styles.wrapper24}>
                    <div className={styles.info8}>Starts in 02:45:12</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div className={styles.block11}>
          <div className={styles.wrapper30}>
            <div className={styles.wrapper31}>
              <div className={styles.wrapper32}>
                <div className={styles.wrapper33}>
                  <div className={styles.wrapper24}>
                    <div className={styles.info9}>Новинки Татарстана</div>
                  </div>

                  <div className={styles.wrapper24}>
                    <div className={styles.info21}>
                      The latest gaming and tech breakthroughs from the local
                      scene.
                    </div>
                  </div>
                </div>

                <img
                  className={styles.image4}
                  src={'/assets/WelcomePage/arrow_icon.svg'}
                  alt="alt text"
                />
              </div>

              <div className={styles.wrapper34}>
                <div className={styles.block13}>
                  <div className={styles.wrapper35}>
                    <div className={styles.wrapper24}>
                      <div
                        className={styles.block14}
                        style={{
                          '--src': `url(${'/assets/WelcomePage/3d_graph.png'})`
                        }}>
                        <div className={styles.grid}>
                          <div className={styles.block15}>
                            <div className={styles.wrapper36}>
                              <img
                                className={styles.image13}
                                src={'/assets/WelcomePage/star_icon.svg'}
                                alt="alt text"
                              />

                              <div className={styles.wrapper37}>
                                <div className={styles.info31}>4.9</div>
                              </div>
                            </div>
                          </div>

                          <div className={styles.wrapper38}>
                            <div className={styles.block16}>
                              <div className={styles.info10}>RPG</div>
                            </div>

                            <div className={styles.block16}>
                              <div className={styles.info111}>TAT</div>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>

                    <div className={styles.wrapper24}>
                      <div className={styles.wrapper39}>
                        <div className={styles.wrapper32}>
                          <div className={styles.wrapper40}>
                            <div className={styles.wrapper24}>
                              <div className={styles.info12}>Shadow Strike</div>
                            </div>

                            <div className={styles.wrapper24}>
                              <div className={styles.info62}>
                                Author: Kazan Studio
                              </div>
                            </div>
                          </div>

                          <img
                            className={styles.image5}
                            src={'/assets/WelcomePage/bookmark_icon.svg'}
                            alt="alt text"
                          />
                        </div>

                        <div className={styles.block18}>
                          <div className={styles.wrapper41}>
                            <div className={styles.wrapper42}>
                              <div className={styles.color1} />

                              <div className={styles.wrapper43}>
                                <div className={styles.block19}>
                                  <div className={styles.info13}>+12</div>
                                </div>
                              </div>
                            </div>

                            <div className={styles.wrapper44}>
                              <div className={styles.info8}>Playing now</div>
                            </div>
                          </div>
                        </div>

                        <div className={styles.wrapper24}>
                          <div className={styles.wrapper45}>
                            <div className={styles.wrapper46}>
                              <div className={styles.block20}>
                                <div className={styles.info14}>92%</div>
                              </div>

                              <div className={styles.wrapper47}>
                                <div className={styles.info8}>Match Rate</div>
                              </div>
                            </div>

                            <div className={styles.block51}>
                              <div className={styles.wrapper48}>
                                <div className={styles.info41}>CLICK</div>
                                <img
                                  className={styles.image14}
                                  src={'/assets/WelcomePage/lightning_icon.svg'}
                                  alt="alt text"
                                />
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>

                <div className={styles.block131}>
                  <div className={styles.wrapper35}>
                    <div className={styles.wrapper24}>
                      <div
                        className={styles.block14}
                        style={{
                          '--src': `url(${'/assets/WelcomePage/tech_shelf.png'})`
                        }}>
                        <div className={styles.grid}>
                          <div className={styles.block15}>
                            <div className={styles.wrapper49}>
                              <img
                                className={styles.image13}
                                src={'/assets/WelcomePage/star_icon.svg'}
                                alt="alt text"
                              />

                              <div className={styles.wrapper37}>
                                <div className={styles.info32}>5.0</div>
                              </div>
                            </div>
                          </div>

                          <div className={styles.wrapper38}>
                            <div className={styles.block161}>
                              <div className={styles.info101}>Classic</div>
                            </div>

                            <div className={styles.block16}>
                              <div className={styles.info111}>TAT</div>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>

                    <div className={styles.wrapper24}>
                      <div className={styles.wrapper39}>
                        <div className={styles.wrapper32}>
                          <div className={styles.wrapper50}>
                            <div className={styles.wrapper24}>
                              <div className={styles.info121}>Retro Runner</div>
                            </div>

                            <div className={styles.wrapper24}>
                              <div className={styles.info6}>
                                Author: Pixel Tat
                              </div>
                            </div>
                          </div>

                          <img
                            className={styles.image6}
                            src={'/assets/WelcomePage/white_bookmark_icon.svg'}
                            alt="alt text"
                          />
                        </div>

                        <div className={styles.block18}>
                          <div className={styles.wrapper41}>
                            <div className={styles.wrapper42}>
                              <div className={styles.color2} />

                              <div className={styles.wrapper43}>
                                <div className={styles.block22}>
                                  <div className={styles.info15}>+8</div>
                                </div>
                              </div>
                            </div>

                            <div className={styles.wrapper44}>
                              <div className={styles.info8}>Playing now</div>
                            </div>
                          </div>
                        </div>

                        <div className={styles.wrapper24}>
                          <div className={styles.wrapper45}>
                            <div className={styles.wrapper46}>
                              <div className={styles.block20}>
                                <div className={styles.info14}>95%</div>
                              </div>

                              <div className={styles.wrapper47}>
                                <div className={styles.info8}>Match Rate</div>
                              </div>
                            </div>

                            <div className={styles.block23}>
                              <div className={styles.wrapper48}>
                                <div className={styles.info16}>CLICK</div>
                                <img
                                  className={styles.image15}
                                  src={
                                    '/assets/WelcomePage/green_lightning_icon.svg'
                                  }
                                  alt="alt text"
                                />
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div className={styles.block241}>
          <div className={styles.wrapper51}>
            <div className={styles.wrapper52}>
              <div className={styles.wrapper53}>
                <div className={styles.wrapper54}>
                  <div className={styles.wrapper55}>
                    <img
                      className={styles.image7}
                      src={'/assets/WelcomePage/gd_logo.png'}
                      alt="alt text"
                    />

                    <div className={styles.wrapper56}>
                      <div className={styles.info17}>GD Evenings</div>
                    </div>
                  </div>

                  <div className={styles.wrapper57}>
                    <p className={styles.info22}>
                      The premier gaming club in Tatarstan. Built by
                      <br />
                      gamers, for gamers. Join the evening revolution and
                      <br />
                      master your craft.
                    </p>
                  </div>

                  <div className={styles.wrapper58}>
                    <img
                      className={styles.image8}
                      src={'/assets/WelcomePage/globe_icon.svg'}
                      alt="alt text"
                    />
                    <img
                      className={styles.image8}
                      src={'/assets/WelcomePage/chat_icon.svg'}
                      alt="alt text"
                    />
                    <img
                      className={styles.image81}
                      src={'/assets/WelcomePage/play_icon.svg'}
                      alt="alt text"
                    />
                  </div>
                </div>

                <div className={styles.wrapper59}>
                  <div className={styles.wrapper24}>
                    <div className={styles.info18}>QUICK LINKS</div>
                  </div>

                  <div className={styles.wrapper60}>
                    <div className={styles.wrapper24}>
                      <div className={styles.info21}>Privacy Policy</div>
                    </div>

                    <div className={styles.wrapper24}>
                      <div className={styles.info21}>Terms of Service</div>
                    </div>

                    <div className={styles.wrapper24}>
                      <div className={styles.info21}>Community Guidelines</div>
                    </div>

                    <div className={styles.wrapper24}>
                      <div className={styles.info21}>Contact Us</div>
                    </div>
                  </div>
                </div>

                <div className={styles.wrapper59}>
                  <div className={styles.wrapper24}>
                    <div className={styles.info18}>CLUB INFO</div>
                  </div>

                  <div className={styles.wrapper61}>
                    <div className={styles.wrapper62}>
                      <img
                        className={styles.image16}
                        src={'/assets/WelcomePage/location_icon.svg'}
                        alt="alt text"
                      />

                      <div className={styles.wrapper63}>
                        <div className={styles.info23}>
                          IT Park Kazan, Tatarstan
                        </div>
                      </div>
                    </div>

                    <div className={styles.wrapper62}>
                      <img
                        className={styles.image17}
                        src={'/assets/WelcomePage/envelope_icon.svg'}
                        alt="alt text"
                      />

                      <div className={styles.wrapper64}>
                        <div className={styles.info21}>hello@gdevenings.ru</div>
                      </div>
                    </div>

                    <div className={styles.wrapper62}>
                      <img
                        className={styles.image18}
                        src={'/assets/WelcomePage/phone_icon.svg'}
                        alt="alt text"
                      />

                      <div className={styles.wrapper65}>
                        <div className={styles.info23}>+7 (843) 123-45-67</div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <div className={styles.block71}>
                <div className={styles.wrapper66}>
                  <div className={styles.wrapper67}>
                    <div className={styles.info21}>
                      © 2024 GD Evenings Gaming Club. All rights reserved.
                    </div>
                  </div>

                  <div className={styles.wrapper68}>
                    <img
                      className={styles.image19}
                      src={'/assets/WelcomePage/check_icon.svg'}
                      alt="alt text"
                    />

                    <div className={styles.wrapper69}>
                      <div className={styles.info21}>
                        Verified Official Community
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default WelcomePage;
