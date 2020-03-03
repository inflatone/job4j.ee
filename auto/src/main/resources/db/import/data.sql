INSERT INTO transmission (id, type)
VALUES (10, 'Automatic'),
       (11, 'Manual');

INSERT INTO engine (id, type)
VALUES (20, 'Diesel'),
       (21, 'Electric'),
       (22, 'Flex-Fuel'),
       (23, 'Gasoline'),
       (24, 'Hybrid'),
       (25, 'Unknown');

INSERT INTO body (id, type)
VALUES (30, 'Sedan'),
       (31, 'SUV / Crossover'),
       (32, 'Pickup Truck'),
       (33, 'Coupe'),
       (34, 'Hatchback'),
       (35, 'Convertible'),
       (36, 'Wagon'),
       (37, 'Minivan');

INSERT INTO vendor(id, name, country)
VALUES (50, 'Mazda', 'Japan'),
       (51, 'BMW', 'Germany'),
       (52, 'Honda', 'Japan'),
       (53, 'Ford', 'USA');

INSERT INTO users(id, name, login, password, role, registered)
VALUES (1, 'User', 'user', 'password', 'USER', '2020-01-03 10:00:00'),
       (2, 'Admin', 'dealer', 'dealer', 'ADMIN', '2019-01-01 10:00:00');

INSERT INTO car (id, vendor_id, model, year, mileage, transmission_id, engine_id, body_id)
VALUES (100, 50, 'Mazda6 Signature', 2019, DEFAULT, 10, 23, 33),
       (101, 51, 'M5', 2008, 54475, 11, 22, 30),
       (102, 52, 'Element EX', 2010, DEFAULT, 10, 22, 34),
       (103, 50, 'MAZDA3 Touring', 2018, 36623, 10, 23, 30),
       (104, 53, 'Taurus SEL', 2015, 61741, 11, 25, 30);

INSERT INTO post(id, user_id, car_id, price, title, posted, message)
VALUES (150, 2, 100, 2440000, 'Best car I have owned', DEFAULT,
        'Jet Black 2019 Mazda Mazda6 Signature FWD Automatic 2.5L 4-Cylinder Turbocharged Mazda6 Signature, 4D Sedan, 2.5L 4-Cylinder Turbocharged, Automatic, FWD, Jet Black, Deep Chestnut Leather, 11 Speakers, 3-Level Heated Reclining Front Sport Bucket Seats, 4-Wheel Disc Brakes, ABS brakes, Air Conditioning, Alloy wheels, AM/FM radio: SiriusXM, Anti-whiplash front head restraints, Auto High-beam Headlights, Auto-dimming door mirrors, Auto-dimming Rear-View mirror, Automatic temperature control, Blind spot sensor: Blind Spot Monitoring System (BSM) warning, Brake assist, Delay-off headlights, Distance pacing cruise control: Mazda Radar Cruise Control (MRCC), Dual front impact airbags, Dual front side impact airbags, Electronic Stability Control, Emergency communication system: MAZDA CONNECT, Four wheel independent suspension, Front anti-roll bar, Front dual zone A/C, Front fog lights, Front reading lights, Fully automatic headlights, Heads-Up Display, Heated door mirrors, Illuminated entry, Leather Shift Knob, Low tire pressure warning, MAZDA CONNECT Infotainment System Voice Command, Memory seat, Nappa Leather Seat Trim, Navigation system: MAZDA CONNECT, Panic alarm, Power door mirrors, Power driver seat, Power moonroof, Power steering, Power windows, Radio data system, Radio: AM/FM/HD, Rain sensing wipers, Rear window defroster, Remote keyless entry, Speed-sensing steering, Steering wheel mounted audio controls, Traction control, Trip computer, Variably intermittent wipers, Ventilated Front Seats.23/31 City/Highway MPG'),
       (151, 1, 101, 1600000, 'One of the fastest and adrenaline rush full size', '2020-03-14 8:13:00',
        'Our 2008 BMW 550i M5 Sedan in Black is a perfect balance between luxury and performance in a stylish package. Powered by a 5.0 Liter V10 that offers world-class performance while connected to a purpose-built quick-shifting Automatic transmission. This Rear Wheel Drive beats most sports cars in performance, all the while delivering the total luxury driving experience you expect from BMW plus offers near 17mpg on the highway. Enjoy confident handling thanks to the M sport suspension, M wheels, and aggressive body lines.The interior of this M5 is a study in efficient ergonomics with a layout that is luxurious and comfortable but also perfectly suited for sport-minded driving. Settle into the 20-way multi-contour heated leather front seats. You''ll appreciate the advanced climate control system, premium audio, full-color navigation, a sunroof, and other top-shelf amenities.It also features BMW''s arsenal of cutting edge safety technologies including traction control, stability control, brake assist, and advanced airbags. Of course, it goes without saying this machine is an absolute joy to drive! Discover the BMW driving experience for yourself. Print this page and call us Now... We Know You Will Enjoy Your Test Drive Towards Ownership! All vehicles go through a mechanical inspection, history check, and a detailing process. We offer high-quality pre-owned automobiles at the most competitive pricing. Come visit our pre-owned line of automobiles in Elmhurst today or call us (630) 279-7000 ...Welcome and thank you for viewing our post we have a wonderful selection of affordable and reliable import vehicles, visit our new redesigned website at www.OkazMotors.com to view more picters and take advantage of our low internet prices, stop by our dealership and test drive the car of your choice. CALL us today at 630-279-7000 for more info.'),
       (152, 1, 102, 550000, 'A toaster I love to drive', '2020-03-10 14:12:00', null),
       (153, 2, 103, 1000000, 'Zippy fun car to drive', '2020-03-06 18:22:00',
        'CARFAX One-Owner. Clean CARFAX. Gray 2018 Mazda Mazda3 Touring FWD 6-Speed SKYACTIV? 2.5L 4-Cylinder DOHC 16V Awards: * 2018 KBB.com Brand Image Awards We Are Your Chicago, IL New and Certified Pre-owned Nissan Dealership near Berwyn, Burbank, Calumet City, Cicero, Elmhurst, Evanston, Franklin Park, La Grange, Matteson, Melrose Park, Morton Grove, Northbrook, Oak Lawn, Oak Park, Orland Park, Tinley Park. Are you wondering, where is Western Ave Nissan or what is the closest Nissan dealer near me? Western Ave Nissan is located at 7410 South Western Ave, Chicago, IL 60636. You can call our Sales Department at , Service Department at , or our Parts Department at . Although Western Ave Nissan in Chicago, Illinois is not open 24 hours a day, seven days a week - our website is always open. On our website, you can research and view photos of the new Nissan models such as the 370Z, Altima, Armada, Frontier, GT-R, LEAF, Maxima, Murano, NV Cargo, NV Passenger, NV200, Pathfinder, Rogue, Rogue Sport, Sentra, Titan, Titan XD, Versa or Versa Note that you would like to purchase or lease. You can also search our entire inventory of new and used vehicles, value your trade-in, and visit our Meet the Staff page to familiarize yourself with our staff who are committed to making your visit to Western Ave Nissan a great experience every time. Test drive today! As a premier Nissan dealer, we have a huge selection of new and used cars, trucks and SUVs, including late model and high-quality pre-owned vehicles. Buy this vehicle with confidence knowing we pulled the CARFAX and the history is clean and problem free. Apply for Financing through our website! Let our experienced team of professionals put you in the car of your dreams!'),
       (154, 2, 104, 975000, 'My car is a great vehicle', '2020-03-15 19:08:00',
        'CarFax Certified! SEL Model! ALL-WHEEL DRIVE! Low Miles! Fully loaded inside with black leather and heated seats, premium touchscreen radio, Bluetooth, sunroof, keyless entry, power locks, power windows, USB/AUX inputs, CD player, premium alloy wheels, and many other options! Powered by a 6-Cylinder Motor, Automatic Transmission, and All-Wheel Drive! Nice condition inside and out, and drives great! CarFax and Clean Title! MIDWESTS VOLUME PREOWNED VW DEALER! Finance Available for Qualifying Customers! Extended Warranties Available for Most Vehicles! Visit Chicago Fine Motors online at ChicagoFineMotors.com to see more pictures of this vehicle or call us at 708-442-5800 today to schedule your test drive.');

