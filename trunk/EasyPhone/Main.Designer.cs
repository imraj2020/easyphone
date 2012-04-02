namespace EasyPhone
{
    partial class Main
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.lblCall = new System.Windows.Forms.Label();
            this.lblExit = new System.Windows.Forms.Label();
            this.SuspendLayout();
            // 
            // lblCall
            // 
            this.lblCall.Font = new System.Drawing.Font("Tahoma", 9F, System.Drawing.FontStyle.Bold);
            this.lblCall.Location = new System.Drawing.Point(3, 20);
            this.lblCall.Name = "lblCall";
            this.lblCall.Size = new System.Drawing.Size(151, 20);
            this.lblCall.Text = "1 - Fazer chamada";
            // 
            // lblExit
            // 
            this.lblExit.Font = new System.Drawing.Font("Tahoma", 9F, System.Drawing.FontStyle.Bold);
            this.lblExit.Location = new System.Drawing.Point(3, 40);
            this.lblExit.Name = "lblExit";
            this.lblExit.Size = new System.Drawing.Size(151, 20);
            this.lblExit.Text = "2 - Sair";
            // 
            // Main
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(96F, 96F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Dpi;
            this.AutoScroll = true;
            this.ClientSize = new System.Drawing.Size(240, 320);
            this.Controls.Add(this.lblExit);
            this.Controls.Add(this.lblCall);
            this.Location = new System.Drawing.Point(0, 0);
            this.Name = "Main";
            this.Text = "Menu inicial";
            this.WindowState = System.Windows.Forms.FormWindowState.Maximized;
            this.Closing += new System.ComponentModel.CancelEventHandler(this.Main_Closing);
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.Label lblCall;
        private System.Windows.Forms.Label lblExit;
    }
}

