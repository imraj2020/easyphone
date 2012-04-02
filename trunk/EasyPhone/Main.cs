using System;
using System.Linq;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using Loquendo;
using System.Collections;
using Microsoft.WindowsMobile.Telephony;

namespace EasyPhone
{
    public partial class Main : Form
    {
        private Timer mScanningTimer = null;
        private int mScanningInterval = 2000;
        private bool mIsScanning = true;
        private int mNumberCycles = 0;
        private ArrayList mOptions;
        private int mCurrentOption = -1;
        private int mNumberOptions = 2;

        public Main()
        {
            InitializeComponent();

            /* INITIALIZE TTS */
            Loquendo6.NewInstance(null);
            Loquendo6.NewDefaultVoice("Eusebio");
            Loquendo6.SetAudio(Loquendo.AudioDestination.AudioBoard, null);

            /* CONFIGURE SCANNING TIMER*/
            mScanningTimer = new Timer();
            mScanningTimer.Interval = mScanningInterval;
            mScanningTimer.Tick += new EventHandler(mScanningTimer_Tick);
            mScanningTimer.Enabled = mIsScanning;

            /* CONFIGURE OPTIONS */
            mOptions = new ArrayList();
            mOptions.Add("1, Fazer chamada.");
            mOptions.Add("2, Sair.");
        }

        void mScanningTimer_Tick(object sender, EventArgs e)
        {
            //next option
            mCurrentOption = (++mCurrentOption) % mNumberOptions;
            if(mCurrentOption == 0 && mNumberCycles == 0)
                readMenuTitle();
            //check if it is last cycle
            if (mNumberCycles == 2) 
                stopScanning();
            else
                readOption(mCurrentOption);

            if (mCurrentOption == mNumberOptions - 1) mNumberCycles++;
        }

        private void readOption(int currentOption)
        {
            if (currentOption >= mOptions.Count) return;
            Loquendo6.Stop();
            Loquendo6.Read(mOptions[currentOption].ToString());
        }

        private void readMenuTitle()
        {
            Loquendo6.Stop();
            Loquendo6.Read(this.Text); //to do: read sync
        }

        private void stopScanning()
        {
            mIsScanning = false;
            mScanningTimer.Enabled = mIsScanning;
            mCurrentOption = -1;
            mNumberCycles = 0;
        }

        private void Main_Closing(object sender, CancelEventArgs e)
        {
            Loquendo6.Stop();
        }

        protected override void OnMouseDown(MouseEventArgs e)
        {
            //base.OnMouseDown(e);
            if (mIsScanning)
            { //select option
                int option = mCurrentOption + 1;
                stopScanning();
                switch(option)
                {
                    case 1: //MAKE CALL
                        Phone p = new Phone();
                        p.Talk("916280481");
                        break;

                    case 2: //EXIT
                        break;
                }
            }
            else
            { //start scanning
                mIsScanning = true;
                mScanningTimer.Enabled = mIsScanning;
            }
        }
    }
}