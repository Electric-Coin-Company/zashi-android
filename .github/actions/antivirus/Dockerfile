FROM clamav/clamav:1.5.1
RUN freshclam
ENTRYPOINT ["clamscan", "--recursive"]
